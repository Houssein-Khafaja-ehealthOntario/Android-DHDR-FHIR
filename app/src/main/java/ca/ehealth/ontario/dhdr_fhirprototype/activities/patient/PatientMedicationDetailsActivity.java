package ca.ehealth.ontario.dhdr_fhirprototype.activities.patient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.models.DHDRMedicationDispenseModel;

/**
 * A simple class that takes in a DHDRMedicationDispenseModel object and inserts the containing data into the
 * Medication Details view for the patient role.
 */
public class PatientMedicationDetailsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medication_details);

        // get medication dispense object that was sent from PatientSummaryActivity
        DHDRMedicationDispenseModel medicationDispense = getIntent().getParcelableExtra("medicationDispense");

        //get the text views for medication dispense details
        TextView dispenseDateTextView = findViewById(R.id.dispenseDateItem);
        TextView brandGenericNameItemTextView = findViewById(R.id.brandGenericNameItem);
        TextView strengthFormItemTextView = findViewById(R.id.strengthFormItem);
        TextView quantityItemTextView = findViewById(R.id.quantityItem);
        TextView dispenseStatusItemTextView = findViewById(R.id.dispenseStatusItem);
        TextView reasonForUseItemTextView = findViewById(R.id.reasonForUseItem);
        TextView dosageInstructionItemTextView = findViewById(R.id.dosageInstructionItem);
        TextView prescriberNameItemTextView = findViewById(R.id.prescriberNameItem);
        TextView prescriberPhoneItemTextView = findViewById(R.id.prescriberPhoneItem);
        TextView pharmacyNameItemTextView = findViewById(R.id.pharmacyNameItem);
        TextView pharmacyPhoneItemTextView = findViewById(R.id.pharmacyPhoneItem);

        // set data to text views
        dispenseDateTextView.setText(medicationDispense.getDispenseDate());
        brandGenericNameItemTextView.setText(medicationDispense.getBrandName());
        strengthFormItemTextView.setText(medicationDispense.getStrength() + " / " + medicationDispense.getForm());
        quantityItemTextView.setText(medicationDispense.getQuantity());
        dispenseStatusItemTextView.setText(medicationDispense.getDispenseStatus());
        reasonForUseItemTextView.setText(medicationDispense.getReasonForUse());
        dosageInstructionItemTextView.setText("Not available with current DHDR version");
        prescriberNameItemTextView.setText(medicationDispense.getPrescriberName());
        prescriberPhoneItemTextView.setText(medicationDispense.getPrescriberPhone());
        pharmacyNameItemTextView.setText(medicationDispense.getPharmacyName());
        pharmacyPhoneItemTextView.setText(medicationDispense.getPharmacyPhone());
    }
}
