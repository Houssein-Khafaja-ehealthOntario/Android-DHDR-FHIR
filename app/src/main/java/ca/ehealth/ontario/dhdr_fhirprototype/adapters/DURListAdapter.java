package ca.ehealth.ontario.dhdr_fhirprototype.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ehealth.ontario.dhdr_fhirprototype.R;

/**
 * This adapter is for the DUR response code, intervention code and text message list views
 */
public class DURListAdapter extends BaseAdapter
{
    private ArrayList<String> durArray;
    private LayoutInflater layoutInflater;
    private Context context;

    /**
     * Nothing fancy about this constructor. Just need to save the data and context,
     * while also inflating our row layout view in order to inject data into it.
     *
     * @param context     the activity that created this adapter (should be PharmaWaitListActivity)
     * @param durArray is an arraylist of dur information
     */
    public DURListAdapter(Context context, ArrayList<String> durArray)
    {
        this.durArray = durArray;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return durArray.size();
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
        return durArray.get(position);
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
            inflatedView = layoutInflater.inflate(R.layout.list_row_dur_info, null);
            holder = new ViewHolder();

            holder.rowText = (TextView) inflatedView.findViewById(R.id.rowText);

            inflatedView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) inflatedView.getTag();
        }

        // insert data into the text views
        holder.rowText.setText(durArray.get(position));

        // set the color of the row for alternating effect
        if (position % 2 == 1)
        {
            inflatedView.setBackgroundColor(inflatedView.getResources().getColor(R.color.colorRowBackground));
        }

        return inflatedView;
    }

    static class ViewHolder
    {
        TextView rowText;
    }
}
