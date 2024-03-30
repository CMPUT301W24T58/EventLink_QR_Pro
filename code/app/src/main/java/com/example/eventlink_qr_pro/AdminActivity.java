package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    Button viewEventsAdminButton;
    Button viewProfilesAdminButton;
    Button viewImagesAdminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        // Initialize Views
        TextView viewEventsAdminButton = findViewById(R.id.view_events_button);
        Button viewProfilesAdminButton = findViewById(R.id.view_profiles_button);
        

        // Set up click listeners for the buttons
        // Adapted from Cejiro's code in AttendeeActivity.java
//        viewEventsAdminButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle "Edit Profile" action here
//                Intent intent = new Intent(AdminActivity.this, EditProfileActivity.class); // TODO: change from editprofileactivity to view event activity
//                //intent.putExtra("attendee", attendee);
//                //startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
//                startActivity(intent);
//            }
//        });

        viewProfilesAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Edit Profile" action here
                Intent intent = new Intent(AdminActivity.this, AdminAttendeeList.class);
                //intent.putExtra("attendee", attendee);
                //startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
                startActivity(intent);
            }
        });


        Button viewImagesButton = findViewById(R.id.view_images_button);

        // Set an OnClickListener to the button
        viewImagesButton.setOnClickListener(view -> {
            // Intent to start the ViewImagesActivity
            Intent intent = new Intent(AdminActivity.this, EventListForAdminImage.class);
            startActivity(intent);
        });

        viewEventsAdminButton.setOnClickListener(view -> {
            // Intent to start the ViewImagesActivity
            Intent intent = new Intent(AdminActivity.this, BrowseDeleteEventAdmin.class);
            startActivity(intent);
        });

    }
}
