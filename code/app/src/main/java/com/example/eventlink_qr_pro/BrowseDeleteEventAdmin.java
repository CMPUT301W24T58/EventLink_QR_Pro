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
import java.util.List;

/**
 * An activity designed for administrators to browse through a list of events. It provides functionalities
 * to fetch and display event names from Firestore and navigate to a detailed view for each event where
 * further actions (like deletion) can be performed.
 */
public class BrowseDeleteEventAdmin extends AppCompatActivity {

    private List<String> eventNameList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;


    /**
     * Initializes the activity, sets up the ListView with an ArrayAdapter, and fetches the list of events from Firestore.
     * It also defines behavior for list item clicks, leading to a detailed view of the selected event.
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
            Intent intent = new Intent(BrowseDeleteEventAdmin.this, BrowseDeleteEventAdminDetail.class);
            intent.putExtra("EVENT_NAME", eventName); // Pass the event name to the detail activity
            startActivity(intent);
        });

        btn_back.setOnClickListener(view -> {
            finish();
        });

    }

    /**
     * Fetches a list of event names from Firestore and updates the ListView adapter.
     * It listens for real-time updates to the events collection to ensure the list remains up-to-date.
     */
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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