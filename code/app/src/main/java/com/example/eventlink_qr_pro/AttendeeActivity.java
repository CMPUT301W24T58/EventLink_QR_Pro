package com.example.eventlink_qr_pro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class AttendeeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final int EDIT_PROFILE_REQUEST_CODE = 100;
    private Attendee attendee;
    private String DeviceID;


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
        Button viewMyEventsButton = findViewById(R.id.btn_view_my_events);
        Intent intent = getIntent();

        getDeviceId(getApplicationContext());
        checkAttendeeExists(DeviceID);

        // Set text for the welcome message
        attendeeNameTextView.setText("Welcome Attendee");



        viewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeActivity.this, BrowseEventsActivity.class);
                intent.putExtra("attendee", attendee);// Assuming AttendeeActivity is the appropriate activity for attendees
                startActivity(intent);
            }
        });




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
                intent.putExtra("attendeeId", attendee.getId());
                startActivity(intent);
            }
        });

        viewMyEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeActivity.this, MyEventsActivity.class);
                intent.putExtra("ATTENDEE_ID", attendee.getId());
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
                if ((attendee.getImageUrl() != null)){

                    Glide.with(this)
                            .asBitmap()
                            .load(attendee.getImageUrl())
                            .apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL)) // Set the size of the loaded image
                            .into(new BitmapImageViewTarget(profilePicImageView) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    // Use the Bitmap resource here
                                    profilePicImageView.setImageBitmap(resource);
                                }
                            });
                } else if (attendee.getImageUrl() == null) {
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
    void checkAttendeeExists(String deviceId) {
        db.collection("attendees")
                .whereEqualTo("deviceId", DeviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Attendee exists, get the first matching attendee
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            attendee = documentSnapshot.toObject(Attendee.class);
                            if (attendee.getImageUrl() != null){
                                Glide.with(this)
                                        .asBitmap()
                                        .load(attendee.getImageUrl())
                                        .apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL)) // Set the size of the loaded image
                                        .into(new BitmapImageViewTarget(profilePicImageView) {
                                            @Override
                                            protected void setResource(Bitmap resource) {
                                                // Use the Bitmap resource here
                                                profilePicImageView.setImageBitmap(resource);

                                            }
                                        });
                            }

                        } else {
                            // Attendee does not exist, create a new attendee and add to the database
                            String attendeeId = UUID.randomUUID().toString();
                            attendee = new Attendee(attendeeId, "", "", "");
                            attendee.setAttendeeEnableTrackingOrNot(true);
                            attendee.setDeviceId(DeviceID);
                            addAttendeeToFirestore(attendee);
                        }
                    } else {
                        Toast.makeText(AttendeeActivity.this, "Failed to check attendee existence", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void getDeviceId(Context context) {
        // Get the device ID
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.DeviceID = deviceId;
    }
}

