package com.example.eventlink_qr_pro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlink_qr_pro.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Activity for displaying a list of events that an attendee is currently registered for and events they have signed up
 * to attend in the future. It queries Firebase Firestore to retrieve the event information.
 */
public class MyEventsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView currentEventsListView, futureEventsListView;
    private ArrayAdapter<String> currentEventsAdapter, futureEventsAdapter;
    private ArrayList<String> currentEventNames = new ArrayList<>();
    private ArrayList<String> futureEventNames = new ArrayList<>();
    private Button backButton;

    /**
     * Initializes the activity, sets up UI components, and fetches both current and future events for the attendee.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It's null the first time the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events_activity);

        String attendeeId = getIntent().getStringExtra("ATTENDEE_ID");

        currentEventsListView = findViewById(R.id.currentEventsListView);
        futureEventsListView = findViewById(R.id.futureEventsListView);

        // Setup ArrayAdapter for current events
        currentEventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentEventNames);
        currentEventsListView.setAdapter(currentEventsAdapter);

        // Setup ArrayAdapter for future events
        futureEventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, futureEventNames);
        futureEventsListView.setAdapter(futureEventsAdapter);

        fetchCurrentEvents(attendeeId);
        fetchFutureEvents(attendeeId);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetches events that the attendee is currently registered for from Firestore and updates the list view
     * to display these events.
     *
     * @param attendeeId The unique ID of the attendee whose current events are being fetched.
     */
    private void fetchCurrentEvents(String attendeeId) {
        db.collection("/events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().collection("attendees")
                                    .document(attendeeId)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult().exists()) {
                                            Event event = document.toObject(Event.class);
                                            currentEventNames.add(event.getName());
                                            currentEventsAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Fetches events that the attendee has signed up for future attendance from Firestore and updates the list view
     * to display these events.
     *
     * @param attendeeId The unique ID of the attendee whose future events are being fetched.
     */
    private void fetchFutureEvents(String attendeeId) {
        db.collection("/events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().collection("Signed Up")
                                    .document(attendeeId)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult().exists()) {
                                            Event event = document.toObject(Event.class);
                                            futureEventNames.add(event.getName());
                                            futureEventsAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
    }
}


