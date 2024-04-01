package com.example.eventlink_qr_pro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class AttendeeAlerts extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView alertsListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> messageTitles;
    private String attendeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_alerts);

        attendeeId = getIntent().getStringExtra("attendeeId");
        alertsListView = findViewById(R.id.alertsListView);
        messageTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageTitles);
        alertsListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchEventMessagesForAttendee();
    }

    private void fetchEventMessagesForAttendee() {
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot eventDocument : task.getResult()) {
                            String eventName = eventDocument.getId();
                            checkIfAttendeeIsInEvent(eventName);
                        }
                    } else {
                        // Handle the error
                    }
                });
    }

    private void checkIfAttendeeIsInEvent(String eventName) {
        db.collection("/events/" + eventName + "/attendees")
                .whereEqualTo("id", attendeeId) // Ensure this matches your attendees' document field
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        fetchMessagesForEvent(eventName);
                    }
                });
    }

    private void fetchMessagesForEvent(String eventName) {
        db.collection("/events/" + eventName + "/messages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot messageDocument : task.getResult()) {
                            // Fetch title and description
                            String title = messageDocument.getString("title");
                            String description = messageDocument.getString("description");

                            String messageEntry = "Event Name: " + eventName + "\nTitle: " + title + "\nDescription: " + description;

                            messageTitles.add(messageEntry);
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
                    }
                });
    }

}

