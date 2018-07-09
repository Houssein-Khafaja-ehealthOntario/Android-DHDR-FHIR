package ca.ehealth.ontario.dhdr_fhirprototype.adapters.patient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.activities.patient.PatientMedicationDetailsActivity;
import ca.ehealth.ontario.dhdr_fhirprototype.models.DHDRMedicationDispenseModel;

/**
 * This adapter is for the Patient Summary (Patient Perspective) Medication Dispense List View.
 * We need this so we can have a well formatted two-column list view with different coloured text.
 */
public class PatientMedicationListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
{
    private ArrayList<DHDRMedicationDispenseModel> medicationDispenses;
    private LayoutInflater layoutInflater;
    private Activity inActivity;

    /**
     * Nothing fancy about this constructor. Just need to save the data and context,
     * while also inflating our row layout view in order to inject data into it.
     *
     * @param activity The activity that created this adapter (should be PatientSummaryActivity)
     * @param medicationDispenses Is an arraylist of patient medication dispenses, with each row item consisting of:
     *                            -Medication Name
     *                            -Classification
     *                            -Next refiled (date and location)
     *                            -Consent directive
     * @param listview The listview to load data into
     */
    public PatientMedicationListAdapter(Activity activity, ArrayList<DHDRMedicationDispenseModel> medicationDispenses, ListView listview)
    {
        inActivity = activity;
        this.medicationDispenses = medicationDispenses;
        layoutInflater = LayoutInflater.from(activity.getApplicationContext());

        listview.setOnItemClickListener(this);
    }

    @Override
    public int getCount()
    {
        return medicationDispenses.size();
    }

    /**
     * Gets an item based on a given position.
     *
     * @param position
     * @return the requested list item object
     */
    @Override
    public Object getItem(int position)
    {
        return medicationDispenses.get(position);
    }

    /**
     * Get list item id with for an item in a certain position.
     * <p>
     * For now, the item ID is also the position number.
     *
     * @param position
     * @return the ID of the requested item.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Here is where we actually insert the data into a row layout.
     * We use a custom class called ViewHolder which will hold all of the text views that hold the data.
     * <p>
     * First we check to see if we already have an inflated row view.
     * If we do, then just reuse our viewHolder and save the previous instance (with getTag). This way we are not calling findViewById again.
     * If we don't, then we initialize it and get fresh text views (medication name, DIN, etc.) for our view holder (setTag).
     * <p>
     * Once we've finished setting/getting the view tag, use the text view references in our ViewHolder to 'setText' the data into the text views
     *
     * @param position     of the list item in the data
     * @param inflatedView the inflated row layout view
     * @param parent
     * @return the inflated row view with data inserted
     */
    @Override
    public View getView(int position, View inflatedView, ViewGroup parent)
    {
        PatientMedicationListAdapter.ViewHolder holder;

        if (inflatedView == null)
        {
            inflatedView = layoutInflater.inflate(R.layout.list_row_patient_dispense, parent, false);
            holder = new PatientMedicationListAdapter.ViewHolder();

            holder.medicationName = (TextView) inflatedView.findViewById(R.id.medicationNameItem);
            holder.classification = (TextView) inflatedView.findViewById(R.id.classificationItem);
            holder.nextRefill = (TextView) inflatedView.findViewById(R.id.nextRefill);
            holder.consentDirective = (TextView) inflatedView.findViewById(R.id.consentDirectiveItem);

            inflatedView.setTag(holder);
        }
        else
        {
            holder = (PatientMedicationListAdapter.ViewHolder) inflatedView.getTag();
        }

        // set the text views
        holder.medicationName.setText(medicationDispenses.get(position).getBrandName());
        holder.classification.setText(medicationDispenses.get(position).getClassification());
        holder.nextRefill.setText("Not available with current DHDR version");
        holder.consentDirective.setText((medicationDispenses.get(position).isConsentGiven()) ? "Not Blocked" : "Blocked");

        // set the color of the row for alternating effect
        if (position % 2 == 1)
        {
            inflatedView.setBackgroundColor(inflatedView.getResources().getColor(R.color.colorRowBackground));
        }

        return inflatedView;
    }

    /**
     * This method will send the user to the PatientMedicationDetailsActivity when they click on a
     * medication dispense from the dispense listview (in PatientSummary)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(inActivity, PatientMedicationDetailsActivity.class);
        intent.putExtra("medicationDispense", medicationDispenses.get(position));
        inActivity.startActivity(intent);
    }

    static class ViewHolder
    {
        TextView medicationName;
        TextView classification;
        TextView nextRefill;
        TextView consentDirective;
    }
}
