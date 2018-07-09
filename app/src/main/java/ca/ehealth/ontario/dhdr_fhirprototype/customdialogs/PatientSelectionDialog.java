package ca.ehealth.ontario.dhdr_fhirprototype.customdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.models.PCRPatientModel;
import ca.ehealth.ontario.dhdr_fhirprototype.services.DHDRAsyncTask;

/**
 * This class is used to show dialogs with a dropdown menu for patients.
 * This is used by the patient role when the patient view button is pressed in MainActivity.
 * Clicking on "start query" will take the user to the PatientSummary activity where the PCR information will be displayed.
 */
public class PatientSelectionDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private Activity context; //the activity context that launched this dialog
    private ArrayList<PCRPatientModel> patients; // list of patients from PCR
    private PCRPatientModel patientToQuery;

    public PatientSelectionDialog(Activity activity)
    {
        super(activity);

        this.context = activity;
    }

    public void setPatients(ArrayList<PCRPatientModel> patients)
    {
        this.patients = patients;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // we are providing our own title
        setContentView(R.layout.custom_dialog_patient_selection);

        // find our buttons
        Button startQueryButton = findViewById(R.id.start_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Spinner patientsDropDown = findViewById(R.id.patientsDropDown);

        //attaching listeners to internal views
        startQueryButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        patientsDropDown.setOnItemSelectedListener(this);

        // get our spinner and attach the data to it via android's built-in array adapter
        ArrayAdapter<PCRPatientModel> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, patients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patientsDropDown.setAdapter(adapter);
    }

    /**
     * simple onClick listener implementation.
     * Will close the dialog when the "cancel" button is pressed.
     * Will start a DHDR async task when the "start query" button is pressed.
     * @param view that was pressed
     */
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.start_button:
                DHDRAsyncTask dhdrAsyncTask = new DHDRAsyncTask(context, patientToQuery);
                dhdrAsyncTask.execute("patient");
                break;

            case R.id.cancel_button:
                dismiss();
                break;

            default:
                dismiss();
                break;
        }
    }

    /**
     * Simple onItemSelected implementation for our spinner dropdown menu
     * @param parent the adapter used with the spinner
     * @param view the list item that was selected
     * @param position the position of the item selected in the arrayList
     * @param id the id of the item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        patientToQuery = patients.get(position);
    }

    /**
     * Simple onNothingSelected implementation for our spinner dropdown menu.
     * Select the first item in the list if nothing was selected.
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        patientToQuery = patients.get(0);
    }
}
