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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity that allows attendees to browse through a list of events. This activity fetches
 * event names from Firestore and displays them in a ListView. Attendees can select an event
 * to view more details about it and potentially sign up or check in.
 */
public class BrowseEventsActivity extends AppCompatActivity {

    private ListView eventsListView;
    private ArrayAdapter<String> adapter;
    private List<String> eventNameList = new ArrayList<>();
    private Button back;
    private Attendee attendee;
    private Map<String, String> eventIdToNameMap = new HashMap<>();

    /**
     * Initializes the activity, sets up the ListView and adapter for displaying event names,
     * and fetches the list of events from Firestore. Also handles navigation to view details
     * of a selected event and allows navigating back to the previous screen.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_browse_events);


        eventsListView = findViewById(R.id.browse_events_list_view);
        back = findViewById(R.id.back_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventNameList);
        eventsListView.setAdapter(adapter);

        fetchEvents();
        Intent intent = getIntent();


        attendee = (Attendee) intent.getSerializableExtra("attendee");


        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEventName = eventNameList.get(position);
            String selectedEventId = null;

            // Retrieve the corresponding event ID using the event name
            for (Map.Entry<String, String> entry : eventIdToNameMap.entrySet()) {
                if (entry.getValue().equals(selectedEventName)) {
                    selectedEventId = entry.getKey();
                    break;
                }
            }
            
            if (selectedEventId != null) {
                Intent intent2 = new Intent(BrowseEventsActivity.this, ViewEventAttendeeActivity.class);
                intent2.putExtra("eventName", selectedEventId);// Pass the event name
                intent2.putExtra("attendee", attendee);
                startActivity(intent2);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    /**
     * Fetches the list of event names from the Firestore database and updates the ListView adapter
     * with this data. Listens for real-time updates to the events collection to ensure the displayed
     * list remains up-to-date.
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
            eventIdToNameMap.clear();
            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    String eventName = document.getString("name"); // Assuming you have a 'name' field
                    if (eventName != null) {
                        eventNameList.add(eventName);
                        eventIdToNameMap.put(document.getId(), eventName);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            } else {
                Log.d("EventListActivity", "Current data: null");
            }
        });
    }
}

