package ca.ehealth.ontario.dhdr_fhirprototype.services;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.activities.pharmacist.PharmaWaitListActivity;
import ca.ehealth.ontario.dhdr_fhirprototype.activities.practitioner.PractitionerAdmissionsListActivity;
import ca.ehealth.ontario.dhdr_fhirprototype.customdialogs.ExceptionErrorDialog;
import ca.ehealth.ontario.dhdr_fhirprototype.customdialogs.PatientSelectionDialog;
import ca.ehealth.ontario.dhdr_fhirprototype.customdialogs.ProgressCircleDialog;
import ca.ehealth.ontario.dhdr_fhirprototype.models.DHDRMedicationDispenseModel;
import ca.ehealth.ontario.dhdr_fhirprototype.models.PCRPatientModel;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

/**
 * This class handles our async task to retrieve the data from the PCR web service
 */
public class PCRAsyncTask extends AsyncTask<String, Void, ArrayList<PCRPatientModel>>
{
    private String userRole;
    private LocalSQLOpenHelper sqLiteOpenHelper;
    private ProgressCircleDialog progressCircleDialog;
    private int exceptionCodeHolder = -69; // to hold onto exceptions thrown in doInBackground() to be processed later in onPostExecute().
    private ExceptionErrorDialog errorDialog; // to display error messages

    // We want to keep a WeakReference to the activity context first, and then when we need it we check to see if it is still valid.
    // This is done to prevent memory leaks which would be caused be using something like: private Context myContext;
    private final WeakReference<Activity> weakReference;

    public PCRAsyncTask(Activity inActivity)
    {
        this.weakReference = new WeakReference<>(inActivity);
        sqLiteOpenHelper = new LocalSQLOpenHelper(inActivity.getApplicationContext());
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

    protected ArrayList<PCRPatientModel> doInBackground(String... strings)
    {
        // initialize the local DB, the results array and save the userRole value
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        ArrayList<PCRPatientModel> results = new ArrayList<>();
        userRole = strings[0]; // the first string is the user role

        // lets loop through the database with a cursor, which requires a projection (our columns)
        String[] projection = {"_id, name, hcn"};
        Cursor cursor = db.query("patients", projection, null, null, null, null, null);

        // DHDRService will be used to make the REST call
        DHDRService dhdrService = new DHDRService();

        while (cursor.moveToNext())
        {
            String healthCardNumber = cursor.getString(cursor.getColumnIndex("hcn"));
            int totalDispenses = 0;
            int durTotal = 0;
            boolean isConsentGiven = true;

            // query PCR and parse xml data
            HttpResponse pcrHttpResponse = PCRService.executeQuery(healthCardNumber);
            PCRPatientModel pcrPatient = PCRService.parseHttpResponse(pcrHttpResponse);

            // in here we will query DHDR, which is only necessary for practitioner view
            if (userRole.equals("practitioner"))
            {
                try
                {
                    Bundle resultBundle = dhdrService.executeQuery(healthCardNumber);
                    totalDispenses = resultBundle.getTotal();

                    // handle use case where 0 dispense are found
                    if (totalDispenses > 0)
                    {
                        // loop through data resources
                        for (Bundle.Entry entry : resultBundle.getEntry())
                        {
                            MedicationDispense medicationDispenseResource = null;
                            OperationOutcome operationOutcomeResource = null;

                            // get medication dispense resource from our result bundle
                            if (entry.getResource() instanceof MedicationDispense)
                            {
                                medicationDispenseResource = (MedicationDispense) entry.getResource();
                            }

                            // get operation outcome resource from our result bundle
                            if (entry.getResource() instanceof OperationOutcome)
                            {
                                operationOutcomeResource = (OperationOutcome) entry.getResource();
                            }

                            // parse data
                            DHDRMedicationDispenseModel medicationDispenseResult = new DHDRMedicationDispenseModel(medicationDispenseResource, operationOutcomeResource);
                            durTotal += medicationDispenseResult.getDurTotal();
                            isConsentGiven = medicationDispenseResult.isConsentGiven();
                        }
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
                    exceptionCodeHolder = 8976; // a random code to indicate a general exception was caught
                }
            }

            // we should have all data here, so lets set it to the pcr object and add to the list
            pcrPatient.setHealthCardNumber(healthCardNumber);
            pcrPatient.setDispenseTotal(totalDispenses);
            pcrPatient.setDurTotal(durTotal);
            pcrPatient.setConsentGiven(isConsentGiven);
            results.add(pcrPatient);
        }

        cursor.close();

        // return the result bundle for onPostExecute
        return results;
    }

    /**
     * This method takes the result and sends it to the ResultFragment.
     * Replaces RequestFragment with ResultFragment.
     * @param results this was retrieved in doInBackround()
     */
    protected void onPostExecute(ArrayList<PCRPatientModel> results)
    {
        // no longer loading, close progress circle
        progressCircleDialog.dismiss();

        Activity activity = weakReference.get();

        // use weak reference to get a strong reference
        //if its no longer valid, then end this task
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
                            ("Client Authentication Error! User needs to provide credentials, has provided invalid credentials, or is not permitted to perform the request operation.\nCode: " + exceptionCodeHolder);
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
                            ("Not found Error! Attempted to locate a resource that has been deleted or did not exist in the first place.\nCode: " + exceptionCodeHolder);
                    break;

                case 0: // FhirClientConnectionException --> SocketTimeOutException
                    errorDialog.showErrorMessage
                            ("Connection Error! Connection timed out. Possible reasons can widely vary. Please try again and ensure you have a proper internet connection. ");
                    break;

                default:
                    errorDialog.showErrorMessage("Unexpected error! Status code: " + exceptionCodeHolder);
                    break;
            }

            // We had an error if we get here
            // So lets end the async task
            return;
        }

        switch (userRole)
        {
            case "patient":
                PatientSelectionDialog patientSelectionDialog = new PatientSelectionDialog(activity);
                patientSelectionDialog.setPatients(results);
                patientSelectionDialog.show();
                break;

            case "practitioner":
                Intent  practitionerIntent = new Intent(activity, PractitionerAdmissionsListActivity.class);
                practitionerIntent.putExtra("patients", results);
                activity.startActivity(practitionerIntent);
                break;

            case "pharmacist":
                Intent  pharmacistIntent = new Intent(activity, PharmaWaitListActivity.class);
                pharmacistIntent.putExtra("patients", results);
                activity.startActivity(pharmacistIntent);
                break;

            default:
                break;
        }
    }
}