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

public class BrowseEventsActivity extends AppCompatActivity {

    private ListView eventsListView;
    private ArrayAdapter<String> adapter;
    private List<String> eventNameList = new ArrayList<>();
    private Button back;
    private Attendee attendee;

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
            // Get the selected event name
            String selectedEvent = eventNameList.get(position);
            // Start a new activity and pass the event name to it
            Intent intent2 = new Intent(BrowseEventsActivity.this, ViewEventAttendeeActivity.class);
            intent2.putExtra("eventName", selectedEvent);// Pass the event name
            intent2.putExtra("attendee", attendee);
            startActivity(intent2);
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

