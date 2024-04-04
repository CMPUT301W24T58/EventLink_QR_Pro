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

public class EventListActivity extends AppCompatActivity {

    private ListView eventsListView;
    private ArrayAdapter<String> adapter;
    private List<String> eventNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);


        eventsListView = findViewById(R.id.events_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventNameList);
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
                    eventNameList.add(document.getId()); // Or document.getString("eventNameField") if you use a specific field for the name
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            } else {
                Log.d("EventListActivity", "Current data: null");
            }
        });
    }
}

