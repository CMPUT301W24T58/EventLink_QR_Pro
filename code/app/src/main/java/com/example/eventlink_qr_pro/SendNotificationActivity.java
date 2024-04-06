package com.example.eventlink_qr_pro;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
/**
 * An activity that allows users (typically organizers) to send notifications related to an event.
 * Users can input a title and description for the notification, which is then stored in Firestore
 * under the specific event's "messages" collection. This could be used for event updates, reminders,
 * or other communications from the event organizers to the attendees.
 */
public class SendNotificationActivity extends AppCompatActivity {
    Button cancelButton, pushButton;
    EditText etToken, etMessageTitle, etMessageDescription;
    String eventName;

    /**
     * Initializes the activity with input fields for the notification's title and description,
     * and buttons to push the notification or cancel the operation. Retrieves the event name from the intent.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It's null the first time the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notification);
        etToken = findViewById(R.id.description);
        eventName = getIntent().getStringExtra("eventName");
        etMessageTitle = findViewById(R.id.notificationTitle);
        etMessageDescription = findViewById(R.id.description);
        cancelButton = findViewById(R.id.cancel);
        pushButton = findViewById(R.id.push);

        cancelButton.setOnClickListener(view -> {
            finish();
        });

        pushButton.setOnClickListener(view -> {
            String messageTitle = etMessageTitle.getText().toString();
            String messageDescription = etMessageDescription.getText().toString();
            pushNotification(eventName, messageTitle, messageDescription);
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();


                    }
                });
    }

    /**
     * Attempts to send a notification message by creating a {@link NotificationMessage} object and adding it
     * to the Firestore under the specified event's "messages" collection. On success, it displays a toast indicating
     * the successful push; on failure, it displays an error message.
     *
     * @param eventName The name of the event to which the notification is related.
     * @param messageTitle The title of the notification message.
     * @param messageDescription The description or body of the notification message.
     */
    private void pushNotification(String eventName, String messageTitle, String messageDescription) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        NotificationMessage message = new NotificationMessage(messageTitle, messageDescription);
        db.collection("/events").document(eventName).collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(this, "Notification pushed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(this, "Error pushing notification", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Nested class to model the structure of a notification message, containing a title and description.
     */
    static class NotificationMessage {
        String title, description;

        /**
         * Constructs a new NotificationMessage with the given title and description.
         *
         * @param title The title of the notification message.
         * @param description The body content of the notification message.
         */
        public NotificationMessage(String title, String description) {
            this.title = title;
            this.description = description;
        }

        /**
         * Gets the title of the notification message.
         *
         * @return The title of the message.
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the description of the notification message.
         *
         * @return The description of the message.
         */
        public String getDescription() {
            return description;
        }
    }
}

