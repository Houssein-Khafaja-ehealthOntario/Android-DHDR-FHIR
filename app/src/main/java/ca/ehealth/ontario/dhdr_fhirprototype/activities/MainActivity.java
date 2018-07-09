/**
 * insert project header stuff here
 */
package ca.ehealth.ontario.dhdr_fhirprototype.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.services.PCRAsyncTask;
import ca.ehealth.ontario.dhdr_fhirprototype.services.PCRService;

/**
 * This Activity represents the landing screen of this prototype.
 * Its only purpose is to display buttons and define the onClick logic of those buttons.
 * There are three buttons: Patient View, Practitioner View and Pharmacist View.
 * Each one should lead to a prompt or a list from which a patient can be selected.
 */
public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * In this scenario, you play the role of a patient trying to find information regarding their
     * drug dispenses. In a real world case, you would require the patient to enter a health card
     * number (and perhaps other information), but for the sake of controlling user input for the
     * prototype, this has been replaced with a drop down menu.
     *
     * This method will start the pcrAsyncTask which will executeQuery health card numbers from our local database and query the PCR
     * to receive demographic data. After it is completed, it will show show a custom dialog implementation which will contain
     * aa dropdown list of patients.
     *
     * This method will be triggered by a button onClick (R.id.patient_view_button).
     * @param view
     */
    public void showPatientViewDialog(View view)
    {
        PCRAsyncTask pcrAsyncTask = new PCRAsyncTask(this);
        pcrAsyncTask.execute("patient");
    }

    /**
     * In this scenario, you play the role of a practitioner trying to look up medication dispense information for someone
     * in ER waiting for emergency care. The practitioner in this case would be more worried about medication history
     * in order to safely provide the required care while avoiding medication conflicts.
     *
     * This method will start the pcrAsyncTask which will executeQuery health card numbers from our local database and query the PCR
     * to receive demographic data. After it is completed, it will start a new activity which will contain a list of patients
     * which will include demographic data (name(from pcr), gender, date of birth, # of dispenses, # of DURs, and the consent directive of the patient record).
     *
     * @param view
     */
    public void showPractitionerViewActivity(View view)
    {
        PCRAsyncTask pcrAsyncTask = new PCRAsyncTask(this);
        pcrAsyncTask.execute("practitioner");
    }

    /**
     * In this scenario, you play the role of a pharmacist trying to dispense medication to people
     * in a waiting list.
     *
     * This method will start the pcrAsyncTask which will executeQuery health card numbers from our local database and query the PCR
     * to receive demographic data. After it is completed, it will start a new activity which will contain a list of patients
     * which will include demographic data (name(from pcr), gender and date of birth).
     *
     * This method will be triggered by a button onClick (R.id.pharmacist_view_button).
     * @param view
     */
    public void showPharmaViewActivity(View view)
    {
        PCRAsyncTask pcrAsyncTask = new PCRAsyncTask(this);
        pcrAsyncTask.execute("pharmacist");
    }
}
