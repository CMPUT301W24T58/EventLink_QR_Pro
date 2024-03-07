package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class UploadImage extends AppCompatActivity {
    ImageView imageToUpload;
    Button bUploadImage;

    // Define an ActivityResultLauncher
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri
                if (uri != null) {
                    imageToUpload.setImageURI(uri);
                    // Further processing with the Uri (e.g., uploading to Firebase)
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_upload);

        imageToUpload = findViewById(R.id.imageToUpload);
        bUploadImage = findViewById(R.id.bUploadImage);

        // Set click listener for the ImageView
        imageToUpload.setOnClickListener(v -> {
            // Launch the activity to get content, specifying the mime type
            mGetContent.launch("image/*");
        });

        // Add click listener for the upload button as needed
        bUploadImage.setOnClickListener(v -> {
            // Code to handle the upload button action
        });
    }
}



