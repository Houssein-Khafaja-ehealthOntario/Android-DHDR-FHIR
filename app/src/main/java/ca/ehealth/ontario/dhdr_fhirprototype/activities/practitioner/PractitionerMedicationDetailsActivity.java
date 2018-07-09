package ca.ehealth.ontario.dhdr_fhirprototype.activities.practitioner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.adapters.DURListAdapter;
import ca.ehealth.ontario.dhdr_fhirprototype.models.DHDRMedicationDispenseModel;

/**
 * A simple class that takes in a DHDRMedicationDispenseModel object and inserts the containing data into the
 * Medication Details view for the practitioner role.
 */
public class PractitionerMedicationDetailsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practitioner_medication_details);

        // get medication dispense object that was sent from PatientSummaryActivity
        DHDRMedicationDispenseModel medicationDispense = getIntent().getParcelableExtra("medicationDispense");

        //get the text views for medication dispense details
        TextView dispenseDateTextView = findViewById(R.id.dispenseDateItem);
        TextView brandGenericNameItemTextView = findViewById(R.id.brandGenericNameItem);
        TextView strengthFormItemTextView = findViewById(R.id.strengthFormItem);
        TextView quantityItemTextView = findViewById(R.id.quantityItem);
        TextView daysSupplyLeftItemTextView = findViewById(R.id.daysSupplyLeftItem);
        TextView reasonForUseItemTextView = findViewById(R.id.reasonForUseItem);
        TextView dosageInstructionItemTextView = findViewById(R.id.dosageInstructionItem);
        TextView prescriberNameItemTextView = findViewById(R.id.prescriberNameItem);
        TextView prescriberPhoneItemTextView = findViewById(R.id.prescriberPhoneItem);

        // set data to text views
        dispenseDateTextView.setText(medicationDispense.getDispenseDate());
        brandGenericNameItemTextView.setText(medicationDispense.getBrandName());
        strengthFormItemTextView.setText(medicationDispense.getStrength() + " / " + medicationDispense.getForm());
        quantityItemTextView.setText(medicationDispense.getQuantity());
        daysSupplyLeftItemTextView.setText(medicationDispense.getDaysSupplyLeft());
        reasonForUseItemTextView.setText(medicationDispense.getReasonForUse());
        dosageInstructionItemTextView.setText("Not available with current DHDR version");
        prescriberNameItemTextView.setText(medicationDispense.getPrescriberName());
        prescriberPhoneItemTextView.setText(medicationDispense.getPrescriberPhone());

        // get DUR list views
        ListView durResponseCodeListView = findViewById(R.id.durResponseCodeListView);
        ListView durInterventionCodeListView = findViewById(R.id.durInterventionCodeListView);
        ListView durMessageListView = findViewById(R.id.durMessageListView);

        // get DUR arraylists
        ArrayList<String> durResponseCodes = medicationDispense.getResponseCodes();
        ArrayList<String> durInterventionCodes = medicationDispense.getInterventionCodes();
        ArrayList<String> durTextMessages = medicationDispense.getDurTextMessages();

        // initialize DUR adapters
        DURListAdapter durResponseCodesAdapter = new DURListAdapter(this, durResponseCodes);
        DURListAdapter durInterventionCodesAdapter = new DURListAdapter(this, durInterventionCodes);
        DURListAdapter durTextMessagesAdapter = new DURListAdapter(this, durTextMessages);

        // set adapters to dur listviews
        durResponseCodeListView.setAdapter(durResponseCodesAdapter);
        durInterventionCodeListView.setAdapter(durInterventionCodesAdapter);
        durMessageListView.setAdapter(durTextMessagesAdapter);

        // enable nested scrolling on listviews
        durResponseCodeListView.setNestedScrollingEnabled(true);
        durInterventionCodeListView.setNestedScrollingEnabled(true);
        durMessageListView.setNestedScrollingEnabled(true);
    }
}
