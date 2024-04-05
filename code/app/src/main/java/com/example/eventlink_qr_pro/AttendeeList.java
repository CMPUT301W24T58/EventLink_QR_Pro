package com.example.eventlink_qr_pro;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AttendeeList extends AppCompatActivity {
    Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_list_cs);

        backButton = findViewById(R.id.back);

        backButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });
        String eventName = getIntent().getStringExtra("eventName"); // Retrieve the event name passed through the intent
        fetchAttendees(eventName);
        fetchFutureSignUps(eventName);
    }

    private void fetchAttendees(String eventName) {
        ListView listView = findViewById(R.id.listview_checked_in_attendees);
        ArrayList<String> attendeeDetails = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeDetails);
        listView.setAdapter(arrayAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("/events/" + eventName + "/attendees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            // Now fetch the check-in count from the document
                            Long checkInCount = document.contains("checkInCount") ? document.getLong("checkInCount") : 0; // Default to 0 if not present
                            String detail = "Name: " + name + ", Check-ins: " + checkInCount;
                            attendeeDetails.add(detail);
                        }
                        arrayAdapter.notifyDataSetChanged(); // Update the ListView with the new data
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void fetchFutureSignUps(String eventName) {
        ListView listViewFutureSignUps = findViewById(R.id.listview_future_sign_ups);
        ArrayList<String> futureSignUpDetails = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, futureSignUpDetails);
        listViewFutureSignUps.setAdapter(arrayAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("/events/" + eventName + "/Signed Up")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            futureSignUpDetails.add(name);
                        }
                        arrayAdapter.notifyDataSetChanged(); // Update the ListView with the new data
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

}
