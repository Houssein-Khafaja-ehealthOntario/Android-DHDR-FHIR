package ca.ehealth.ontario.dhdr_fhirprototype.services;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import ca.ehealth.ontario.dhdr_fhirprototype.activities.PatientSummaryActivity;
import ca.ehealth.ontario.dhdr_fhirprototype.customdialogs.ExceptionErrorDialog;
import ca.ehealth.ontario.dhdr_fhirprototype.customdialogs.ProgressCircleDialog;
import ca.ehealth.ontario.dhdr_fhirprototype.models.PCRPatientModel;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

/**
 * This class handles our async task to retrieve the data from the DHDR service
 */
public class DHDRAsyncTask extends AsyncTask<String, Void, Bundle>
{
    // We want to keep a WeakReference to the activity context first, and then when we need it we check to see if it is still valid.
    // This is done to prevent memory leaks which would be caused be using something like: private Context myContext;
    private final WeakReference<Activity> weakReference;
    private String userRole;
    private PCRPatientModel patientToQuery;
    private ProgressCircleDialog progressCircleDialog; // to display loading dialog
    private ExceptionErrorDialog errorDialog; // to display error messages
    private int exceptionCodeHolder = -69; // to hold onto exceptions thrown in doInBackground() to be processed later in onPostExecute().
    private boolean isUpdatingData = false; // flag indicating whether this query is replacing old data with new data

    /**
     * Simple constructor that saves a weak reference of launching activity and the patient object to be queried.
     * Also does some initializations with custom dialog objects.
     * @param inActivity the activity that started the async task
     * @param patientToQuery the patient to query
     */
    public DHDRAsyncTask(Activity inActivity, PCRPatientModel patientToQuery)
    {
        this.weakReference = new WeakReference<>(inActivity);
        this.patientToQuery = patientToQuery;

        // start our progress circle
        progressCircleDialog = new ProgressCircleDialog(inActivity);

        // this dialog will be used to handle errors
        errorDialog = new ExceptionErrorDialog(inActivity);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        progressCircleDialog.show();
    }

    /**
     * This function will start a new instance of the DHDRService and use that to query DHDR.
     * It will use the health card number from the patientToQuery object
     * @param strings an array of string parameters which are passed in with the .execute() function from the async task.
     * @return a FHIR Bundle containing the medication dispenses
     */
    protected Bundle doInBackground(String... strings)
    {
        // userRole is the first string passed in
        userRole = strings[0];

        // DHDRService will be used to make the REST call
        DHDRService dhdrService = new DHDRService();

        // Our response will come back as a FHIR Bundle
        Bundle dhdrQueryResults = null;

        // Try to query DHDR
        try
        {
            String healthCardNumber = patientToQuery.getHealthCardNumber();

            // if we have a start and end date query strings
            if(strings.length == 3)
            {
                dhdrQueryResults = dhdrService.executeQuery(healthCardNumber, strings[1], strings[2]);
                isUpdatingData = true;
            }
            else
            {
                dhdrQueryResults = dhdrService.executeQuery(healthCardNumber);
            }

            if (dhdrQueryResults.getEntry() == null || dhdrQueryResults.getEntry().isEmpty())
            {
                // data was not found
                exceptionCodeHolder = 404;
            }
        }
        // catch the FHIR exceptions and save their codes
        catch (BaseServerResponseException e)
        {
            Log.d("silly", e.toString());
            exceptionCodeHolder = e.getStatusCode();
        }
        catch (Exception e)
        {
            Log.d("silly", e.toString());
            exceptionCodeHolder = 17438; // a random code to indicate a general exception was caught
        }

        // return the result bundle for onPostExecute
        return dhdrQueryResults;
    }

    /**
     * @param result this was retrieved from doInBackround()
     */
    protected void onPostExecute(Bundle result)
    {
        // no longer loading, close progress circle dialog
        progressCircleDialog.dismiss();

        // Use weak reference of the launching activity to get a strong reference
        //if its no longer valid, then end this task
        Activity activity = weakReference.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed())
        {
            // activity is no longer valid, don't do anything!
            return;
        }

        // check if we got any exceptions during doInBackground()
        if (exceptionCodeHolder != -69)
        {
            switch (exceptionCodeHolder)
            {
                case 401: // AuthenticationException
                case 403: // ForbiddenOperationException
                    errorDialog.showErrorMessage
                    ("Client Authentication Error! User needs to provide credentials, has provided invalid credentials, or is not permitted to perform the request operation.\nCode: " + exceptionCodeHolder );
                    break;

                case 500:// InternalErrorException
                    errorDialog.showErrorMessage
                    ("Server Error! The server failed to successfully process the request. This generally means that the server is misbehaving or is misconfigured in some way.\nCode: " + exceptionCodeHolder);
                    break;

                case 400: // InvalidRequestException
                case 405: // MethodNotAllowedException
                case 501: // NotImplementedOperationException
                case 422: // UnprocessableEntityException
                    errorDialog.showErrorMessage("Client Error! The client's message was not valid, or the requested method has been disabled/not implemented by the server.\nCode: " + exceptionCodeHolder);
                    break;

                case 410: // ResourceGoneException
                case 404: // ResourceNotFoundException
                    errorDialog.showErrorMessage
                    ("Not found Error! Attempt to locate a resource that has been deleted or did not exist in the first place.\nCode: " + exceptionCodeHolder);
                    break;

                case 0: // FhirClientConnectionException --> SocketTimeOutException
                    errorDialog.showErrorMessage
                    ("Connection Error! Connection timed out. Possible reasons can widely vary. Please try again and ensure you  have a proper internet connection. ");
                    break;

                default:
                    errorDialog.showErrorMessage("Unexpected error! Status code: " + exceptionCodeHolder);
                    break;
            }

            // We had an error if we get here
            // So lets end the task
            return;
        }

        // handling the result in here
        try
        {
            // Our PatientSummaryActivity is a modular activity that accommodates all three of our roles (patient, pharmacist, practitioner)
            Intent patientSummaryIntent = new Intent(activity, PatientSummaryActivity.class);

            // check operation outcome
            if(result.getEntry() instanceof OperationOutcome)
            {
                // get Issues array, check each code for "not found"
                for(OperationOutcome.Issue outcome : ((OperationOutcome) result.getEntry()).getIssue())
                {
                    // clear the listview if no results are found
                    if (outcome.getCode().equals("not-found"))
                    {
                        if (isUpdatingData)
                        {
                            ((PatientSummaryActivity) activity).setListViewNoResults();
                        }

                        return; // no point in moving forward
                    }
                }
            }

            // if not updating data, then start a new activity with the result
            if(!isUpdatingData)
            {
                // Our DHDR service has a bundleToString method which makes it easier to move the data to a new activity
                patientSummaryIntent.putExtra("medicationDispense", DHDRService.BundleToString(result));
                patientSummaryIntent.putExtra("userRole", userRole);
                patientSummaryIntent.putExtra("patient", patientToQuery); // we need to display the demographics data in the next activity

                activity.startActivity(patientSummaryIntent);
            }
            // since we're updating data, no need to start a new activity. Just call setListViewData to update the data
            else
            {
                ((PatientSummaryActivity) activity).setListViewData(result);
            }
        }
        catch (Exception e)
        {
            Log.d("test", "An unhandled exception got got.");
        }
    }
}