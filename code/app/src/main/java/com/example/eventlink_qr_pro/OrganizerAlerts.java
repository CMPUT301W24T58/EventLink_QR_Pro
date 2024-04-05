package com.example.eventlink_qr_pro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerAlerts extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listView;
    private AlertAdapter adapter;
    private List<Alert> alertsList;
    private String eventName;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_alerts);

        listView = findViewById(R.id.alerts_list_view);
        back = findViewById(R.id.back_button);
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
        setupMilestoneListener(eventName);

        back.setOnClickListener(view -> {
            finish();
        });
    }


    private void setupMilestoneListener(String eventName) {
        db.collection("events").document(eventName).collection("milestones")
                .orderBy("timestamp") // Assuming you have a timestamp field to order by
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(OrganizerAlerts.this, "Error loading milestones.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Alert> newAlerts = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.get("count") != null && doc.get("timestamp") != null) {
                            long count = doc.getLong("count");
                            com.google.firebase.Timestamp timestamp = doc.getTimestamp("timestamp");
                            String formattedDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp.toDate());

                            String message = "Milestone reached with " + count + " attendees!";
                            newAlerts.add(new Alert(message, formattedDate));
                        }
                    }

                    // Update the list and notify the adapter
                    alertsList.clear();
                    alertsList.addAll(newAlerts);
                    adapter.notifyDataSetChanged();
                });
    }



}



