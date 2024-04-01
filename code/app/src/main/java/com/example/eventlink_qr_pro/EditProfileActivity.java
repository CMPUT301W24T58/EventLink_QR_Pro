package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.example.eventlink_qr_pro.Attendee;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText nameEditText, emailEditText, phoneEditText;
    private Button chooseImageButton, saveButton, cancelButton, removeButton;
    private Uri filePath;

    private FirebaseFirestore db;
    private Bitmap bitmap;
    private Attendee attendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.profPicImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        removeButton = findViewById(R.id.removeImageButton);

        // Retrieve Attendee object from intent
        attendee = (Attendee) getIntent().getSerializableExtra("attendee");

        if (attendee.getImageUrl() != null){
            Glide.with(this)
                    .asBitmap()
                    .load(attendee.getImageUrl())
                    .apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL)) // Set the size of the loaded image
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            // Use the Bitmap resource here
                            imageView.setImageBitmap(resource);
                            bitmap = resource;
                        }
                    });
        }

        // Populate fields with Attendee data
        nameEditText.setText(attendee.getName());
        emailEditText.setText(attendee.getEmail());
        phoneEditText.setText(attendee.getPhoneNumber());

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
                attendee.clearImageByteArray();
                attendee.setImageUrl(null);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if ((attendee.getImageUrl()) != null){
                 //   uploadImageToStorage();
                //}
                saveProfile();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                this.bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadImageToStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            name = "";
        } else if (TextUtils.isEmpty(email)) {
            email = "";

        } else if (TextUtils.isEmpty(phone)) {
            phone ="";

        }

        // Update Attendee object with new data
        attendee.setName(name);
        attendee.setEmail(email);
        attendee.setPhoneNumber(phone);




        saveOrUpdateAttendee(attendee);
        // Return updated Attendee object to calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedAttendee", attendee);
        setResult(RESULT_OK, resultIntent);
        finish();

    }

    private void uploadImageToStorage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + attendee.getId() + ".jpg");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(byteArray);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                attendee.setImageUrl(imageUrl);
                Toast.makeText(EditProfileActivity.this, "uploaded image", Toast.LENGTH_SHORT).show();

            });
        }).addOnFailureListener(exception -> {
            // Handle failed upload
            Toast.makeText(EditProfileActivity.this, "Failed to upload image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveOrUpdateAttendee(Attendee attendee) {
        db.collection("attendees").document(attendee.getId())
                .update("name", attendee.getName(), "email", attendee.getEmail(), "phone", attendee.getPhoneNumber(), "imageUrl", attendee.getImageUrl())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
