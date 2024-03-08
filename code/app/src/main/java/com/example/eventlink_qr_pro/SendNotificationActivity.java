package com.example.eventlink_qr_pro;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SendNotificationActivity extends AppCompatActivity {
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notification); // The layout file for sending notifications

        cancelButton = findViewById(R.id.cancel);

        cancelButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });
    }
}

