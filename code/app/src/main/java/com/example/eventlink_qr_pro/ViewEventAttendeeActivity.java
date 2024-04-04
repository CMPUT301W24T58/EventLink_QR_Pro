package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
    private Attendee attendee;

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
        Intent intent = getIntent();


        attendee = (Attendee) intent.getSerializableExtra("attendee");
        eventName = intent.getStringExtra("eventName");

        if (eventName != null) {
            loadEventData(eventName);
        }

        signupButton.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events")
                    .whereEqualTo("name", eventName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Get the ID of the event document
                                String eventId = document.getId();

                                DocumentReference attendeeRef = db.collection("events").document(eventId)
                                        .collection("Signed Up").document(attendee.getId());

                                attendeeRef.get().addOnCompleteListener(attendeeTask -> {
                                    if (attendeeTask.isSuccessful() && attendeeTask.getResult() != null) {
                                        DocumentSnapshot attendeeDocument = attendeeTask.getResult();
                                        if (attendeeDocument.exists()) {
                                            // Document exists
                                            Toast.makeText(ViewEventAttendeeActivity.this, "Already signed up for this event", Toast.LENGTH_SHORT).show();
                                        } else {

                                            attendeeRef.set(attendee);
                                            Toast.makeText(ViewEventAttendeeActivity.this, "Successfully signed up for this event", Toast.LENGTH_SHORT).show();
                                            finish();

                                        }
                                    } else {
                                        // Handle errors or document does not exist scenarios
                                        Toast.makeText(ViewEventAttendeeActivity.this, "Error fetching attendee data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(ViewEventAttendeeActivity.this, "Failed to find event", Toast.LENGTH_SHORT).show();
                        }
                    });


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

