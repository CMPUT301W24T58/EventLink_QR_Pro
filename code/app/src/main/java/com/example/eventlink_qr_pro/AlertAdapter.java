package com.example.eventlink_qr_pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

/**
 * An ArrayAdapter subclass for displaying {@link Alert} objects in a ListView or a similar view.
 * This adapter is responsible for converting Alert objects into View items loaded into the list container.
 * It inflates custom layout for each item based on the organizer_alerts_items.xml layout file.
 */
public class AlertAdapter extends ArrayAdapter<Alert> {

    /**
     * Constructs a new {@link AlertAdapter}.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param alerts A List of {@link Alert} objects to display in a list.
     */
    public AlertAdapter(Context context, List<Alert> alerts) {
        super(context, 0, alerts);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the list item view.
     * @param convertView The recycled view to populate. If null, a new view is inflated.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.organizer_alerts_items, parent, false);
        }

        // Lookup view for data population
        TextView alertMessage = convertView.findViewById(R.id.textView_alert_message);
        TextView alertDate = convertView.findViewById(R.id.textView_alert_date);

        // Get the data item for this position
        Alert alert = getItem(position);

        // Populate the data into the template view using the data object
        alertMessage.setText(alert.getMessage());
        alertDate.setText(alert.getDate());

        // Return the completed view to render on screen
        return convertView;
    }
}

