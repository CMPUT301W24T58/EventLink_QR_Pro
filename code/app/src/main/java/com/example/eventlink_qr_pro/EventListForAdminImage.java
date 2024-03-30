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

public class EventListForAdminImage extends AppCompatActivity {

    private List<String> eventNameList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list_admin);

        listView = findViewById(R.id.events_list_view);
        Button btn_back = findViewById(R.id.btn_back);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventNameList);
        listView.setAdapter(adapter);

        fetchEvents(); // Call your method to fetch events

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String eventName = eventNameList.get(position); // Get the clicked event's name
            Intent intent = new Intent(EventListForAdminImage.this, EventDetailAdmin.class);
            intent.putExtra("EVENT_NAME", eventName); // Pass the event name to the detail activity
            startActivity(intent);
        });

        btn_back.setOnClickListener(view -> {
            finish();
        });

    }

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
                    eventNameList.add(document.getId()); // Or use a field name
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            } else {
                Log.d("EventListActivity", "Current data: null");
            }
        });
    }
}

