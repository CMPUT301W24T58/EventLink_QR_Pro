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
/**
 * An activity that displays a list of alerts for organizers based on specific milestones reached within an event,
 * such as a certain number of attendees registering. The activity listens for real-time updates to milestone data
 * stored in Firestore and updates the UI accordingly.
 */
public class OrganizerAlerts extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listView;
    private AlertAdapter adapter;
    private List<Alert> alertsList;
    private String eventName;
    private Button back;
    /**
     * Initializes the activity by setting the content view, retrieving and setting up the ListView for displaying
     * alerts, and fetching the event name from the intent. If the event name is not provided, the activity will display
     * a toast message and finish. It also sets up a listener to fetch and display milestone alerts from Firestore.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It's null the first time the activity is created.
     */
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

    /**
     * Sets up a real-time listener for the specified event's milestones collection in Firestore. For each milestone document,
     * it constructs an alert with a message indicating the milestone reached and the time it was reached, then updates the UI.
     *
     * @param eventName The name of the event for which milestones are being monitored.
     */
    private void setupMilestoneListener(String eventName) {
        db.collection("events").document(eventName).collection("milestones")
                .orderBy("timestamp") 
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



