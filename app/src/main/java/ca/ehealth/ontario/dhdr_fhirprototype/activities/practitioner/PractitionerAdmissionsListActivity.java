package ca.ehealth.ontario.dhdr_fhirprototype.activities.practitioner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.activities.PatientSummaryActivity;
import ca.ehealth.ontario.dhdr_fhirprototype.adapters.practitioner.PractitionerERListAdapter;
import ca.ehealth.ontario.dhdr_fhirprototype.models.PCRPatientModel;
import ca.ehealth.ontario.dhdr_fhirprototype.services.DHDRAsyncTask;

/**
 * This activity will display a list of patients that are meant to be in an ER admissions list.
 *
 * An ArrayList of PCR patients will be fed into a custom adapter which handles how the
 * ListView will display the data.
 */
public class PractitionerAdmissionsListActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practitioner_admissions_list);

        // get the patients sent from the async task
        ArrayList<PCRPatientModel> patients = getIntent().getExtras().getParcelableArrayList("patients");

        // initialise listview and data array
        ListView patientListView = findViewById(R.id.medication_dispenses_list);

        // attach data to a the new adapter and then attach adapter to the ListView
        PractitionerERListAdapter adapter = new PractitionerERListAdapter(this, patients, patientListView);
        patientListView.setAdapter(adapter);
    }
}
