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
        setContentView(R.layout.attendee_list); // The layout file for sending notifications

        backButton = findViewById(R.id.back);

        backButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });
        String eventName = getIntent().getStringExtra("eventName"); // Retrieve the event name passed through the intent
        fetchAttendees(eventName);
    }

    private void fetchAttendees(String eventName) {
        ListView listView = findViewById(R.id.listview_attendees); // Adjust the ID as per your layout
        ArrayList<String> attendeeDetails = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeDetails);
        listView.setAdapter(arrayAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("/events/" + eventName + "/attendees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name"); // Assuming there's a 'name' field
                            String email = document.getString("email"); // Assuming there's an 'email' field
                            // Combine or format the details as needed for display
                            String detail = "Name: " + name;
                            attendeeDetails.add(detail);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }


}
