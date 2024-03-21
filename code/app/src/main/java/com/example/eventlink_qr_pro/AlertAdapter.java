package com.example.eventlink_qr_pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

public class AlertAdapter extends ArrayAdapter<Alert> {

    public AlertAdapter(Context context, List<Alert> alerts) {
        super(context, 0, alerts);
    }

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
        // You can also set icons if needed, or handle icon clicks

        // Return the completed view to render on screen
        return convertView;
    }
}

