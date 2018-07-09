package ca.ehealth.ontario.dhdr_fhirprototype.activities.pharmacist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.adapters.pharmacist.PharmaWaitListAdapter;
import ca.ehealth.ontario.dhdr_fhirprototype.models.PCRPatientModel;
import ca.ehealth.ontario.dhdr_fhirprototype.services.DHDRAsyncTask;

/**
 * This activity will display a list of patients that are meant to be in a waiting list.
 *
 * An ArrayList of PCR patients will be fed into a custom adapter which handles how the
 * ListView will display the data.
 */
public class PharmaWaitListActivity extends AppCompatActivity
{
    private ArrayList<PCRPatientModel> patients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharma_wait_list);

        // get the patients sent from the async task
        patients = getIntent().getExtras().getParcelableArrayList("patients");

        // initialise listview
        ListView patientListView = findViewById(R.id.medication_dispenses_list);

        // attach data to a the new adapter and then attach adapter to the ListView
        PharmaWaitListAdapter adapter = new PharmaWaitListAdapter(this, patients, patientListView);
        patientListView.setAdapter(adapter);
    }
}
