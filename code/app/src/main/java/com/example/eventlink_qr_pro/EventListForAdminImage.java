package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity for administrators to view a list of all events. This list is fetched from Firebase Firestore
 * and displayed in a ListView. Administrators can select an event to view its detailed information or manage the event.
 */
public class EventListForAdminImage extends AppCompatActivity {

    private List<String> eventNameList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Map<String, String> eventIdToNameMap = new HashMap<>();

    /**
     * Sets up the activity's layout and UI components. Initializes the ListView adapter with event names
     * fetched from Firestore. Sets up a listener for list item clicks, which navigates to the event's detailed view,
     * and a listener for the back button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list_admin);

        listView = findViewById(R.id.events_list_view);
        Button btn_back = findViewById(R.id.btn_back);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventNameList);
        listView.setAdapter(adapter);

        fetchEvents(); 

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String eventName = eventNameList.get(position); // Get the clicked event's name
            String eventId = eventIdToNameMap.get(eventName);
            if (eventId != null) {
                Intent intent = new Intent(EventListForAdminImage.this, EventDetailAdmin.class);
                intent.putExtra("EVENT_NAME", eventId); // Pass the event name to the detail activity
                startActivity(intent);
            } else {
                Log.d("EventListForAdminImage", "Event ID not found for name: " + eventName);
            }
        });

        btn_back.setOnClickListener(view -> {
            finish();
        });

    }

    /**
     * Fetches a list of all events from Firebase Firestore and updates the ListView adapter.
     * Utilizes a snapshot listener to ensure the list is updated in real-time with any changes.
     */
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("EventListActivity", "Listen failed.", error);
                return;
            }

            eventNameList.clear(); // Clear the existing list
            eventIdToNameMap.clear();
            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    String eventName = document.getString("name"); // Extract the event name
                    if (eventName != null) {
                        eventNameList.add(eventName); // Add the name to the list for the adapter
                        eventIdToNameMap.put(eventName, document.getId()); // Map event ID to name for lookup
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            } else {
                Log.d("EventListActivity", "Current data: null");
            }
        });
    }
}

