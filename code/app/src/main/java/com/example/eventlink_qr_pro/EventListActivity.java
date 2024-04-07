package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of events fetched from Firebase Firestore. Users can select an event to view its details
 * or create a new event. This activity utilizes a ListView to display the events and a dialog fragment for
 * creating new events.
 */
public class EventListActivity extends AppCompatActivity {

    private ListView eventsListView;
    private ArrayAdapter<String> adapter;
    private List<String> eventNameList = new ArrayList<>();

    /**
     * Initializes the activity, sets up the ListView and its adapter, and fetches the list of events from Firestore.
     * It also sets up an onItemClickListener for the ListView to navigate to the detail view of the selected event and
     * a click listener for the create event button to open the dialog fragment for event creation.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It's null the first time the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);


        eventsListView = findViewById(R.id.events_list_view);
        adapter = new ArrayAdapter<>(this, R.layout.custom_event_list_item, R.id.text_event_name, eventNameList);
        eventsListView.setAdapter(adapter);

        fetchEvents();
        Button createEventButton = findViewById(R.id.create_event_button);

        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected event name
            String selectedEvent = eventNameList.get(position);
            // Start a new activity and pass the event name to it
            Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
            intent.putExtra("eventName", selectedEvent); // Pass the event name
            startActivity(intent);
        });

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventDialogFragment dialogFragment = new CreateEventDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "createEvent");
            }
        });

    }
    /**
     * Fetches the list of event names from Firestore and updates the ListView adapter with this data.
     * Attaches a snapshot listener to the events collection to ensure the displayed list remains up-to-date.
     */
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Attaching a snapshot listener to the collection
        db.collection("events").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("EventListActivity", "Listen failed.", error);
                return;
            }

            eventNameList.clear(); // Clear the existing list
            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    eventNameList.add(document.getId());
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            } else {
                Log.d("EventListActivity", "Current data: null");
            }
        });
    }
}

