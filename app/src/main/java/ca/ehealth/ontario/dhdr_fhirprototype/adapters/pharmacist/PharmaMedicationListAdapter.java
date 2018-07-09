package ca.ehealth.ontario.dhdr_fhirprototype.adapters.pharmacist;

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
import ca.ehealth.ontario.dhdr_fhirprototype.activities.pharmacist.PharmaMedicationDetailsActivity;
import ca.ehealth.ontario.dhdr_fhirprototype.models.DHDRMedicationDispenseModel;

/**
 * This adapter is for the Patient Summary (Pharmacist Perspective) Medication Dispense List View.
 * We need this so we can have a well formatted two-column list view with different coloured text.
 */
public class PharmaMedicationListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
{
    private ArrayList<DHDRMedicationDispenseModel> medicationDispenses;
    private LayoutInflater layoutInflater;
    private Activity inActivity;

    /**
     * Nothing fancy about this constructor. Just need to save the data and context,
     * while also inflating our row layout view in order to inject data into it.
     *
     * @param activity            the activity that created this adapter (should be PatientSummaryActivity)
     * @param medicationDispenses is an arraylist of patient medication dispenses, with each row item consisting of:
     *                            -Medication Name
     *                            -Dispense Date
     *                            -DIN
     *                            -DUR Response Codes
     *                            -Pharmacy Name
     *                            -Refills left
     */
    public PharmaMedicationListAdapter(Activity activity, ArrayList<DHDRMedicationDispenseModel> medicationDispenses, ListView listview)
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
     * This is known as the ViewHolder pattern, which eliminates unnecessary UI thread computation by not calling findViewById for every single list item.
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
        PharmaMedicationListAdapter.ViewHolder holder;

        if (inflatedView == null)
        {
            inflatedView = layoutInflater.inflate(R.layout.list_row_pharma_dispense, parent, false);
            holder = new PharmaMedicationListAdapter.ViewHolder();

            holder.medicationName = (TextView) inflatedView.findViewById(R.id.medicationNameItem);
            holder.dispenseDate = (TextView) inflatedView.findViewById(R.id.dispenseDateItem);
            holder.DIN = (TextView) inflatedView.findViewById(R.id.dinItem);
            holder.durTypes = (TextView) inflatedView.findViewById(R.id.durItem);
            holder.pharmacyName = (TextView) inflatedView.findViewById(R.id.pharmacyNameItem);
            holder.refillsLeft = (TextView) inflatedView.findViewById(R.id.refillsLeftItem);

            inflatedView.setTag(holder);
        }
        else
        {
            holder = (PharmaMedicationListAdapter.ViewHolder) inflatedView.getTag();
        }

        holder.medicationName.setText(medicationDispenses.get(position).getBrandName());
        holder.dispenseDate.setText(medicationDispenses.get(position).getDispenseDate());
        holder.DIN.setText(medicationDispenses.get(position).getDrugIdentificationNumber());
        holder.durTypes.setText(medicationDispenses.get(position).getConcatedDURs());
        holder.pharmacyName.setText(medicationDispenses.get(position).getPharmacyName());
        holder.refillsLeft.setText("Not available with current DHDR version");

        // set the color of the row for alternating effect
        if (position % 2 == 1)
        {
            inflatedView.setBackgroundColor(inflatedView.getResources().getColor(R.color.colorRowBackground));
        }

        return inflatedView;
    }

    /**
     * This method will send the user to the PharmaMedicationDetailsActivity when they click on a
     * medication dispense from the dispense listview (in PatientSummary)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(inActivity, PharmaMedicationDetailsActivity.class);
        intent.putExtra("medicationDispense", medicationDispenses.get(position));
        inActivity.startActivity(intent);
    }

    static class ViewHolder
    {
        TextView medicationName;
        TextView dispenseDate;
        TextView DIN;
        TextView durTypes;
        TextView pharmacyName;
        TextView refillsLeft;
    }
}
