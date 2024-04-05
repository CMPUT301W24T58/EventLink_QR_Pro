package com.example.eventlink_qr_pro;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadImage extends AppCompatActivity {
    private ImageView imageToUpload;
    private Button bUploadImage;
    private Uri imageUri; // Uri of the selected image
    private String eventName; // Event name to be retrieved from the intent

    // Firebase Storage reference
    private StorageReference storageReference;

    // Define an ActivityResultLauncher
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri
                if (uri != null) {
                    imageUri = uri; // Assign it to imageUri
                    imageToUpload.setImageURI(uri);
                    // The user can now choose to upload this image
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_upload);

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference("event_images");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        imageToUpload = findViewById(R.id.imageToUpload);
        bUploadImage = findViewById(R.id.bUploadImage);

        // Retrieve the event name from the intent
        eventName = getIntent().getStringExtra("eventName");

        if (eventName == null || eventName.trim().isEmpty()) {
            Toast.makeText(this, "Event name is required.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no event name is provided
            return;
        }

        db.collection("events").document(eventName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String imageUrl = task.getResult().getString("imageUrl");
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    // If imageUrl exists, load it into the ImageView
                    Glide.with(this).load(imageUrl).into(imageToUpload);
                } else {
                    // If no imageUrl, set the default placeholder
                    imageToUpload.setImageResource(R.drawable.default_placeholder);
                }
            } else {
                // On failure or if document doesn't exist, set the default placeholder
                imageToUpload.setImageResource(R.drawable.default_placeholder);
            }
        });

        // Set click listener for the ImageView to select an image
        imageToUpload.setOnClickListener(v -> mGetContent.launch("image/*"));

        // Add click listener for the upload button to handle the upload action
        bUploadImage.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase();
                finish();
            } else {
                Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Use a consistent name for the image. For example, you can name the image file as the eventName itself.
            // Ensure the eventName is suitable to be used in a file path (e.g., no forbidden characters).
            // This example assumes eventName is valid. Consider adding checks or sanitization as necessary.
            final String imageName = eventName + "." + getFileExtension(imageUri); // This will overwrite the existing image with the same eventName.

            StorageReference fileReference = storageReference.child(eventName + "/" + imageName); // The path includes eventName for organization.

            UploadTask uploadTask = fileReference.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                String imageUrl = downloadUri.toString();
                saveImageInfoToFirestore(imageUrl); // Save or update Firestore document with new image URL
                Toast.makeText(UploadImage.this, "Upload successful", Toast.LENGTH_SHORT).show();
            })).addOnFailureListener(e -> Toast.makeText(UploadImage.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageInfoToFirestore(String imageUrl) {
        FirebaseFirestore.getInstance().collection("events").document(eventName)
                .update("imageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(UploadImage.this, "Image uploaded successfully", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(UploadImage.this, "Failed to upload image details: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }



}






