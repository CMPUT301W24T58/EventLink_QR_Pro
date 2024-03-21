package com.example.eventlink_qr_pro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerAlerts extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listView;
    private AlertAdapter adapter;
    private List<Alert> alertsList;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_alerts);

        listView = findViewById(R.id.alerts_list_view);
        alertsList = new ArrayList<>();
        adapter = new AlertAdapter(this, alertsList);
        listView.setAdapter(adapter);

        // Retrieve the event name from the intent
        eventName = getIntent().getStringExtra("eventName");
        if (eventName == null || eventName.trim().isEmpty()) {
            Toast.makeText(this, "Event name not provided.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if event name is not present
            return;
        }

        // Set the title of the activity to include the event name for context
        setTitle("Alerts for " + eventName);

        // Listen for changes in the number of attendees for the given event
        setupAttendeeListener(eventName);
    }

    private void setupAttendeeListener(String eventName) {
        db.collection("events").document(eventName).collection("attendees")
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(OrganizerAlerts.this, "Error listening to event updates.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        int currentCount = snapshots.size();

                        // Only show alerts for every 5 attendees (5, 10, 15, 20, ...)
                        if (currentCount % 5 == 0) {
                            // Get the timestamp of the last attendee to determine the milestone time
                            DocumentSnapshot lastAttendee = snapshots.getDocuments().get(snapshots.size() - 1);
                            com.google.firebase.Timestamp timestamp = lastAttendee.getTimestamp("timestamp");
                            String formattedDate = "Just now"; // Default message in case of null
                            if (timestamp != null) {
                                // Format the timestamp to a more readable form
                                formattedDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp.toDate());
                            }

                            String message = "Congratulations! Milestone reached with " + currentCount + " attendees!";
                            // Use the formattedDate as the time when the milestone was reached
                            alertsList.add(0, new Alert(message, formattedDate));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }



}


