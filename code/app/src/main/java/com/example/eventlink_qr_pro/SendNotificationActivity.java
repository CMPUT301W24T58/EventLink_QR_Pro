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
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;


public class SendNotificationActivity extends AppCompatActivity {
    Button cancelButton, pushButton;
    EditText etToken, etMessageTitle, etMessageDescription;
    String eventName;

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

        FirebaseApp.initializeApp(this);

        cancelButton.setOnClickListener(view -> {
            finish();
        });

        pushButton.setOnClickListener(view -> {
            String messageTitle = etMessageTitle.getText().toString();
            String messageDescription = etMessageDescription.getText().toString();
            pushNotification(eventName, messageTitle, messageDescription);
        });
    }

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

    static class NotificationMessage {
        String title, description;

        public NotificationMessage(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}

