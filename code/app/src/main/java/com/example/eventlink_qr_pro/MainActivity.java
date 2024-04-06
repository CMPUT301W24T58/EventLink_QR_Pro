package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.UUID;

/**
 * The main activity that acts as the entry point for the application. It allows users to navigate to different sections
 * of the app based on their role: Attendee, Organizer, or Admin. Additionally, it retrieves the device's FCM token
 * for push notifications.
 */
public class MainActivity extends AppCompatActivity {


    private FirebaseFirestore db;

    /**
     * Sets up the activity's user interface and initializes Firebase Firestore. Defines the behavior for each role's
     * button (Attendee, Organizer, Admin) to navigate to the corresponding activity. Also, it retrieves and logs the
     * device's FCM token.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        Button attendeeButton = findViewById(R.id.attendee_button);
        Button adminButton = findViewById(R.id.administrator_button);
        Button organizerButton = findViewById(R.id.organizer_button);

        // Set an OnClickListener for the Attendee button
        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AttendeeActivity.class);

                startActivity(intent);

            }
        });

        // Set an OnClickListener for the Organizer button
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start RegisterQRActivity
                Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });

        // Set an OnClickListener for the Admin button
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start AdminActivity
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        // Get FCM Token
        getFMCToken();
    }

    /**
     * Retrieves the FCM token for the device and logs it. This token can be used for sending push notifications
     * to this specific device.
     */
    void getFMCToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.i("My token", token);
            }
        });
    }


}
