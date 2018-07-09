package ca.ehealth.ontario.dhdr_fhirprototype.activities.pharmacist;

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
 * Medication Details view for the Pharmacist role.
 */
public class PharmaMedicationDetailsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharma_medication_details);

        // get medication dispense object that was sent from PatientSummaryActivity
        DHDRMedicationDispenseModel medicationDispense = getIntent().getParcelableExtra("medicationDispense");

        //get the text views for medication dispense details
        TextView dispenseDateTextView = findViewById(R.id.dispenseDateItem);
        TextView brandGenericNameItemTextView = findViewById(R.id.brandGenericNameItem);
        TextView strengthFormItemTextView = findViewById(R.id.strengthFormItem);
        TextView quantityItemTextView = findViewById(R.id.quantityItem);
        TextView daysSupplyItemTextView = findViewById(R.id.daysSupplyItem);
        TextView dispenseStatusItemTextView = findViewById(R.id.dispenseStatusItem);
        TextView therapeuticClassItemTextView = findViewById(R.id.therapeuticClassItem);
        TextView experationDateItemTextView = findViewById(R.id.experationDateItem);
        TextView requestStatusItemTextView = findViewById(R.id.requestStatusItem);
        TextView dinItemTextView = findViewById(R.id.dinItem);
        TextView prescriptionNumberItemTextView = findViewById(R.id.prescriptionNumberItem);
        TextView therapeuticSubClassItemTextView = findViewById(R.id.therapeuticSubClassItem);
        TextView reasonForUseItemTextView = findViewById(R.id.reasonForUseItem);
        TextView dosageInstructionItemTextView = findViewById(R.id.dosageInstructionItem);
        TextView prescriberNameItemTextView = findViewById(R.id.prescriberNameItem);
        TextView prescriberPhoneItemTextView = findViewById(R.id.prescriberPhoneItem);
        TextView prescriberIdItemTextView = findViewById(R.id.prescriberIdItem);
        TextView pharmacyNameItemTextView = findViewById(R.id.pharmacyNameItem);
        TextView pharmacyPhoneItemTextView = findViewById(R.id.pharmacyPhoneItem);
        TextView pharmacistNameItemTextView = findViewById(R.id.pharmacistNameItem);
        TextView refillsLeft = findViewById(R.id.refillsItem);

        // set data to text views
        dispenseDateTextView.setText(medicationDispense.getDispenseDate());
        brandGenericNameItemTextView.setText(medicationDispense.getBrandName());
        strengthFormItemTextView.setText(medicationDispense.getStrength() + " / " + medicationDispense.getForm());
        quantityItemTextView.setText(medicationDispense.getQuantity());
        daysSupplyItemTextView.setText(medicationDispense.getDaysSupply());
        dispenseStatusItemTextView.setText(medicationDispense.getDispenseStatus());
        therapeuticClassItemTextView.setText(medicationDispense.getClassification());
        experationDateItemTextView.setText("Not available with current DHDR version");
        requestStatusItemTextView.setText("Not available with current DHDR version");
        dinItemTextView.setText(medicationDispense.getDrugIdentificationNumber());
        prescriptionNumberItemTextView.setText(medicationDispense.getPrescriptionNumber());
        therapeuticSubClassItemTextView.setText(medicationDispense.getSubClassification());
        reasonForUseItemTextView.setText(medicationDispense.getReasonForUse());
        dosageInstructionItemTextView.setText("Not available with current DHDR version");
        prescriberNameItemTextView.setText(medicationDispense.getPrescriberName());
        prescriberPhoneItemTextView.setText(medicationDispense.getPrescriberPhone());
        prescriberIdItemTextView.setText(medicationDispense.getPrescriberId());
        pharmacyNameItemTextView.setText(medicationDispense.getPharmacyName());
        pharmacyPhoneItemTextView.setText(medicationDispense.getPharmacyPhone());
        pharmacistNameItemTextView.setText(medicationDispense.getPharmacistName());
        refillsLeft.setText("Not available with current DHDR version");

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

        // enable nested scrolling on listviews so that lists can be scrolled properly in a scrollable screen
        durResponseCodeListView.setNestedScrollingEnabled(true);
        durInterventionCodeListView.setNestedScrollingEnabled(true);
        durMessageListView.setNestedScrollingEnabled(true);
    }
}
