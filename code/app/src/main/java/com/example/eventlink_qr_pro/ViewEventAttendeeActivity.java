package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class ViewEventAttendeeActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;
    private Button signupButton;
    private Button cancelButton;



    private FirebaseFirestore db;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_view_event_details);
        db = FirebaseFirestore.getInstance();

        // Initialize your views here
        eventNameEditText = findViewById(R.id.event_name_edit_text_attendee);
        eventDateEditText = findViewById(R.id.event_date_edit_text_attendee);
        eventLocationEditText = findViewById(R.id.event_location_edit_text_attendee);
        eventDescriptionEditText = findViewById(R.id.event_description_edit_text_attendee);
        signupButton = findViewById(R.id.update_button_attendee);
        cancelButton = findViewById(R.id.cancel_button_attendee);


        // Retrieve the event name from the intent
        eventName = getIntent().getStringExtra("eventName");

        if (eventName != null) {
            loadEventData(eventName);
        }

        signupButton.setOnClickListener(view -> {

            finish();
        });

        cancelButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });




    }

    private void loadEventData(String eventName) {
        DocumentReference eventRef = db.collection("events").document(eventName);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                eventNameEditText.setText(documentSnapshot.getString("name"));
                eventDateEditText.setText(documentSnapshot.getString("date"));
                eventLocationEditText.setText(documentSnapshot.getString("location"));
                eventDescriptionEditText.setText(documentSnapshot.getString("description"));
            }
        }).addOnFailureListener(e -> {
            // Handle the error here
        });
    }



}

