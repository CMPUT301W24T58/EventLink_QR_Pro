package com.example.eventlink_qr_pro;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AttendeeList extends AppCompatActivity {
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_list); // The layout file for sending notifications

        backButton = findViewById(R.id.back);

        backButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });
    }
}
