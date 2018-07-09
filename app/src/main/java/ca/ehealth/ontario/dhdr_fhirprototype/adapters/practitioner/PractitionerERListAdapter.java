package ca.ehealth.ontario.dhdr_fhirprototype.adapters.practitioner;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.R;
import ca.ehealth.ontario.dhdr_fhirprototype.models.DHDRMedicationDispenseModel;
import ca.ehealth.ontario.dhdr_fhirprototype.models.PCRPatientModel;
import ca.ehealth.ontario.dhdr_fhirprototype.services.DHDRAsyncTask;

/**
 * This adapter is for the ER Admissions List view (Practitioner Perspective). We need this so we can have a well formatted
 * two-column list view with different coloured text.
 */
public class PractitionerERListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
{
    private ArrayList<PCRPatientModel> patientList;
    private LayoutInflater layoutInflater;
    private Activity inActivity;

    /**
     * Nothing fancy about this constructor. Just need to save the data and context,
     * while also inflating our row layout view in order to inject data into it.
     *
     * @param activity    the activity that created this adapter (should be PharmaWaitListActivity)
     * @param patientData is an arraylist of patient demographics(Name, Gender, Date of birth)
     */
    public PractitionerERListAdapter(Activity activity, ArrayList<PCRPatientModel> patientData, ListView listview)
    {
        inActivity = activity;
        this.patientList = patientData;
        layoutInflater = LayoutInflater.from(activity.getApplicationContext());

        listview.setOnItemClickListener(this);
    }

    @Override
    public int getCount()
    {
        return patientList.size();
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
        return patientList.get(position);
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
        ViewHolder holder;

        if (inflatedView == null)
        {
            inflatedView = layoutInflater.inflate(R.layout.list_row_practitioner_patientlist, null);
            holder = new ViewHolder();

            holder.name = (TextView) inflatedView.findViewById(R.id.nameItem);
            holder.gender = (TextView) inflatedView.findViewById(R.id.genderItem);
            holder.dateOfBirth = (TextView) inflatedView.findViewById(R.id.dobItem);
            holder.totalDispenses = (TextView) inflatedView.findViewById(R.id.dispensesItem);
            holder.totalDURs = (TextView) inflatedView.findViewById(R.id.durItem);
            holder.isConsentProvided = (ImageView) inflatedView.findViewById(R.id.isConsentImageView);

            inflatedView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) inflatedView.getTag();
        }


        // These are the images representing yes/no consent directives
        Drawable youShallNotPass = inflatedView.getResources().getDrawable(android.R.drawable.presence_busy);
        Drawable youShallPass = inflatedView.getResources().getDrawable(android.R.drawable.presence_online);

        // insert data into the text views
        holder.name.setText(patientList.get(position).getName());
        holder.dateOfBirth.setText(patientList.get(position).getDateOfBirth());
        holder.gender.setText(patientList.get(position).getGender());
        holder.totalDispenses.setText(String.valueOf(patientList.get(position).getDispenseTotal()));
        holder.totalDURs.setText(String.valueOf(patientList.get(position).getDurTotal()));
        holder.isConsentProvided.setImageDrawable((patientList.get(position).getConsentGiven() ? youShallPass : youShallNotPass));

        // set the color of the row for alternating effect
        if (position % 2 == 1)
        {
            // id for the even-row background color
            int rowBackgroundColor = inflatedView.getResources().getColor(R.color.colorRowBackground);
            inflatedView.setBackgroundColor(rowBackgroundColor);
        }

        // These are the resources needed to show a red circle instead of a default white circle (purple border)
        Drawable redCircle = inflatedView.getResources().getDrawable(R.drawable.red_circle);
        int whiteColorId = inflatedView.getResources().getColor(android.R.color.white); //to be used when setting text color

        // we want to show red warnings when total durs > 0
        if (patientList.get(position).getDurTotal() > 0)
        {
            holder.totalDURs.setBackground(redCircle);
            holder.totalDURs.setTextColor(whiteColorId);
        }

        // we want to show red warnings when total dispenses > 0
        if (patientList.get(position).getDispenseTotal() > 0)
        {
            holder.totalDispenses.setBackground(redCircle);
            holder.totalDispenses.setTextColor(whiteColorId);
        }

        return inflatedView;
    }

    /**
     * This method will start a new DHDR Async task which will take the user to PatientSummaryActivity
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        DHDRAsyncTask dhdrAsyncTask = new DHDRAsyncTask(inActivity, patientList.get(position));
        dhdrAsyncTask.execute("practitioner");
    }

    static class ViewHolder
    {
        TextView name;
        TextView gender;
        TextView dateOfBirth;

        // these will need a DHDR call
        TextView totalDispenses;
        TextView totalDURs;
        ImageView isConsentProvided;
    }
}
