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
import com.google.firebase.messaging.FirebaseMessaging;

public class SendNotificationActivity extends AppCompatActivity {
    Button cancelButton;

    EditText etToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notification); // The layout file for sending notifications
        etToken = findViewById(R.id.description);


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

                        // Log and toast

                        System.out.println(token);
                        Toast.makeText(SendNotificationActivity.this, "Your device registration token is " + token
                                , Toast.LENGTH_SHORT).show();

                        etToken.setText(token);

                    }
                });
        cancelButton = findViewById(R.id.cancel);

        cancelButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });
    }
}

