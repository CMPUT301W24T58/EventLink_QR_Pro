package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button viewImagesButton = findViewById(R.id.view_images_button);

        // Set an OnClickListener to the button
        viewImagesButton.setOnClickListener(view -> {
            // Intent to start the ViewImagesActivity
            Intent intent = new Intent(AdminActivity.this, EventListForAdminImage.class);
            startActivity(intent);
        });

    }
}
