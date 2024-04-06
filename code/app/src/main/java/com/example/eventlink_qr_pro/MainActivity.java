package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MainActivity serves as the entry point for the application, guiding users to different app sections
 * based on their role: Attendee, Organizer, or Admin. It handles the retrieval and local storage of
 * the device's Firebase Cloud Messaging (FCM) token for push notifications and controls access to the Admin
 * section based on the device's FCM token and predefined administrator limits.
 */
public class MainActivity extends AppCompatActivity {


    private FirebaseFirestore db;

    /**
     * Initializes the user interface and Firebase services. Sets up navigation for different user roles
     * and retrieves the device's FCM token for push notification purposes. It also checks and manages access to
     * administrative features based on the FCM token and a set limit on the number of administrators.
     *
     * @param savedInstanceState Contains the most recent data supplied in onSaveInstanceState(Bundle) if the activity
     *                           is re-initialized after previously being shut down. Otherwise, it is null.
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
                checkAdminAccessAndProceed();
            }
        });

        // Get FCM Token
        getFMCToken();
    }

    /**
     * Retrieves and locally stores the device's FCM token using Firebase Messaging Service. This token
     * is essential for sending targeted push notifications to this specific device.
     */
    void getFMCToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.i("My token", token);
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fcm_token", token);
                editor.apply();
            }
        });
    }

    /**
     * Checks the device's FCM token against the stored list of administrator tokens in Firestore.
     * It controls access to AdminActivity based on whether the token is listed or if the limit of
     * administrators has not been reached. New administrators are added to Firestore if under the limit.
     */
    void checkAdminAccessAndProceed() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentToken = sharedPreferences.getString("fcm_token", "defaultToken");
        final int adminLimit = 15; // admin limit

        db.collection("administrators").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int adminCount = 0;
                boolean isTokenAdmin = false;
                for (DocumentSnapshot document : task.getResult()) {
                    adminCount++;
                    if (document.getId().equals(currentToken)) {
                        isTokenAdmin = true;
                        break;
                    }
                }

                if (isTokenAdmin || adminCount < adminLimit) {
                    if (!isTokenAdmin) {
                        // Add the FCM token as a new administrator
                        Map<String, Object> adminData = new HashMap<>();
                        adminData.put("createdAt", System.currentTimeMillis());
                        db.collection("administrators").document(currentToken).set(adminData);
                    }
                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "You cannot access admin features.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("Admin Check", "Failed to check admin access", task.getException());
                Toast.makeText(MainActivity.this, "Error checking admin access.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
