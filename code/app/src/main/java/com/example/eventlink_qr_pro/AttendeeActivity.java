package com.example.eventlink_qr_pro;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class AttendeeActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_main_menu);
        db = FirebaseFirestore.getInstance();

        // Initialize views
        ImageView profilePicImageView = findViewById(R.id.profilePicImageView);
        TextView attendeeNameTextView = findViewById(R.id.attendee_name_text_view);
        Button scanCodeButton = findViewById(R.id.btn_scan_code);
        Button viewEventsButton = findViewById(R.id.btn_view_events_joined);
        Button attendeeAlertsButton = findViewById(R.id.btn_attendee_alerts);
        Button editProfileButton = findViewById(R.id.btn_edit_profile);
        Button backButton = findViewById(R.id.back_button);
        Intent intent = getIntent();


        Attendee attendee = (Attendee) intent.getSerializableExtra("attendee");

        // Display the attendee details
        if (attendee != null) {
            // Set the attendee name in the TextView
            attendeeNameTextView.setText(attendee.getName());
            addAttendeeToFirestore(attendee);
        }


        // Set text for the welcome message
        //attendeeNameTextView.setText("Welcome Attendee");

        // Set click listener for the back button to return to the previous menu
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current AttendeeActivity and return to the previous menu
                finish();
            }
        });

        // Set click listener for the editProfileButton to handle the "Edit Profile" action
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Edit Profile" action here
            }
        });

        scanCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeActivity.this, QRCodeScannerActivity.class); // Assuming AttendeeActivity is the appropriate activity for attendees
                intent.putExtra("attendee", attendee);
                startActivity(intent);
            }
        });

        attendeeAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeActivity.this, AttendeeAlerts.class); // Assuming AttendeeActivity is the appropriate activity for attendees
                intent.putExtra("attendee", attendee);
                startActivity(intent);
            }
        });

    }
    private void addAttendeeToFirestore(Attendee attendee) {
        // Add attendee data to Firestore
        // For example, you can create a collection named "attendees" and add the attendee document
        // Replace "attendees" with your desired collection name
        db.collection("attendees")
                .document(attendee.getId())
                .set(attendee)
                .addOnSuccessListener(aVoid -> Log.d("CreateEvent", "Event successfully written!"))
                .addOnFailureListener(e -> Log.w("CreateEvent", "Error adding event", e));
    }
}

