package com.example.eventlink_qr_pro;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static final int EDIT_PROFILE_REQUEST_CODE = 100;
    private Attendee attendee;


    ImageView profilePicImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_main_menu);
        db = FirebaseFirestore.getInstance();

        // Initialize views
        profilePicImageView = findViewById(R.id.profilePicImageView);
        TextView attendeeNameTextView = findViewById(R.id.attendee_name_text_view);
        Button scanCodeButton = findViewById(R.id.btn_scan_code);
        Button viewEventsButton = findViewById(R.id.btn_view_events_joined);
        Button attendeeAlertsButton = findViewById(R.id.btn_attendee_alerts);
        Button editProfileButton = findViewById(R.id.btn_edit_profile);
        Button backButton = findViewById(R.id.back_button);
        Intent intent = getIntent();


        this.attendee = (Attendee) intent.getSerializableExtra("attendee");
        addAttendeeToFirestore(attendee);

        // Set text for the welcome message
        attendeeNameTextView.setText("Welcome Attendee");




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
                Intent intent = new Intent(AttendeeActivity.this, EditProfileActivity.class); // Assuming AttendeeActivity is the appropriate activity for attendees
                intent.putExtra("attendee", attendee);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d("AttendeeActivity", "onActivityResult called");
                this.attendee = (Attendee) data.getSerializableExtra("updatedAttendee");
                if (attendee.getImageByteArray() != null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(attendee.getImageByteArray(), 0, attendee.getImageByteArray().length);
                    profilePicImageView.setImageBitmap(bitmap);
                } else if (attendee.getImageByteArray() == null) {
                    profilePicImageView.setImageDrawable(null);

                }


            }
        }
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

