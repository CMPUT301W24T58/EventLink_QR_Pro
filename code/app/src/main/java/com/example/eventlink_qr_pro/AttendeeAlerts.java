package com.example.eventlink_qr_pro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class AttendeeAlerts extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_alerts);

        String eventName = getIntent().getStringExtra("eventName"); // Ensure this is passed from the previous activity
        ListView alertsListView = findViewById(R.id.alertsListView);
        ArrayList<String> messageTitles = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageTitles);
        alertsListView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("/events/" + eventName + "/messages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            messageTitles.add(message.getTitle()); // Assuming you want to display the titles
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
                    } else {
                        // Handle the error
                    }
                });
    }
}
