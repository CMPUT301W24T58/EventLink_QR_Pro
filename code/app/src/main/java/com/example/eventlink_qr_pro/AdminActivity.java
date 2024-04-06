package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
/**
 * The {@code AdminActivity} class extends {@link AppCompatActivity} and serves as a control center for administrators
 * within the application. It provides UI elements that allow administrators to view and manage different aspects of
 * the event link application, such as events, profiles, and images. This activity acts as a navigation hub to various
 * functionalities specific to the administrative role.
 */
public class AdminActivity extends AppCompatActivity {
    Button viewEventsAdminButton;
    Button viewProfilesAdminButton;
    Button viewImagesAdminButton;

    /**
     * Called when the activity is starting. This method initializes the activity, its views, and event listeners.
     * It sets up the UI components from the XML layout file and configures click listeners for navigation buttons,
     * facilitating navigation to functionalities specific to administrators such as viewing events, profiles, and images.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        // Initialize Views
        TextView viewEventsAdminButton = findViewById(R.id.view_events_button);
        Button viewProfilesAdminButton = findViewById(R.id.view_profiles_button);
        
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
