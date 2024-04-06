package com.example.eventlink_qr_pro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * An activity that displays a list of event-related alerts or messages for a specific attendee. It queries
 * the Firestore database to retrieve messages from events that the attendee is part of. Each message
 * includes details such as the event name, message title, and description.
 */
public class AttendeeAlerts extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView alertsListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> messageTitles;
    private String attendeeId;
    private Button back;

    /**
     * Initializes the activity, retrieves the attendee ID passed via intent, and sets up the ListView
     * to display the fetched messages. It also initializes the Firestore instance and fetches the event messages.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_alerts);

        attendeeId = getIntent().getStringExtra("attendeeId");
        alertsListView = findViewById(R.id.alertsListView);
        back = findViewById(R.id.back_button);
        messageTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageTitles);
        alertsListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchEventMessagesForAttendee();

        back.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Fetches event messages for the attendee by first checking which events the attendee is part of,
     * then retrieving messages for those events. This method queries the "events" collection and iterates
     * through each event document to check attendee participation.
     */
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

    /**
     * Checks if the attendee is part of the specified event by querying the subcollection of attendees
     * under the event document. If the attendee is part of the event, it proceeds to fetch messages for that event.
     *
     * @param eventName The name of the event to check the attendee's participation in.
     */
    private void checkIfAttendeeIsInEvent(String eventName) {
        db.collection("/events/" + eventName + "/attendees")
                .whereEqualTo("id", attendeeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        fetchMessagesForEvent(eventName);
                    }
                });
    }

    /**
     * Fetches messages for the specified event from Firestore and adds them to the ListView adapter for display.
     * Each message's title and description are retrieved and formatted for presentation.
     *
     * @param eventName The name of the event for which to fetch messages.
     */
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

