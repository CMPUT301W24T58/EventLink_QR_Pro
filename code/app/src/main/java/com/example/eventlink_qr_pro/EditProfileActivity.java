package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.example.eventlink_qr_pro.Attendee;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText nameEditText, emailEditText, phoneEditText;
    private Button chooseImageButton, saveButton, cancelButton, removeButton, generateButton;
    private Uri filePath;
    private Switch enableTrackingSwitch;
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
        generateButton = findViewById(R.id.createImageButton);
        enableTrackingSwitch = findViewById(R.id.trackingSwitch);

        // Retrieve Attendee object from intent
        attendee = (Attendee) getIntent().getSerializableExtra("attendee");

        // Assuming 'attendeeId' is the ID of the attendee document in Firestore
        String attendeeId = attendee.getId(); // Or however you get the attendee's ID

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference attendeeRef = db.collection("attendees").document(attendeeId);

        attendeeRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Assuming 'imageUrl' is the field name in Firestore where the image URL is stored
                String imageUrl = documentSnapshot.getString("imageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Use Glide to load the image from the URL
                    Glide.with(this)
                            .asBitmap()
                            .load(imageUrl)
                            .apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL)) // Set the size of the loaded image
                            .into(new BitmapImageViewTarget(imageView) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    // Use the Bitmap resource here
                                    imageView.setImageBitmap(resource);
                                    // Assuming 'bitmap' is a Bitmap variable in your class
                                    bitmap = resource;
                                }
                            });
                }
            } else {
                // Handle the case where the attendee document does not exist
                Log.d("Firestore", "No such attendee document!");
            }
        }).addOnFailureListener(e -> {
            // Handle any errors that occur during the fetching process
            Log.e("Firestore", "Error fetching attendee document", e);
        });


        // Populate fields with Attendee data
        nameEditText.setText(attendee.getName());
        emailEditText.setText(attendee.getEmail());
        phoneEditText.setText(attendee.getPhoneNumber());

        enableTrackingSwitch.setChecked(attendee.isAttendeeEnableTrackingOrNot());
        enableTrackingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the Attendee's tracking preference
            attendee.setAttendeeEnableTrackingOrNot(isChecked);
        });
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

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Generate SHA-256 hash
                String hash = generateSHA256(attendee.getId() + ":" + attendee.getName());
                // Generate the shape based image from hash
                Bitmap defaultImage = generateShapeBasedImageFromHash(hash);
                // Upload the bitmap to storage
                imageView.setImageBitmap(defaultImage);
                Toast.makeText(EditProfileActivity.this, "please wait, uploading to database", Toast.LENGTH_SHORT).show();
                uploadBitmapToStorage(defaultImage, attendee.getId());
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
                Toast.makeText(EditProfileActivity.this, "please wait, uploading to database", Toast.LENGTH_SHORT).show();
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
        boolean enableTracking = enableTrackingSwitch.isChecked();

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
        attendee.setAttendeeEnableTrackingOrNot(enableTracking);


        updateEventsCollection(attendee);
        updateAttendeeTrackingPreferenceAcrossEvents(attendee.getId(), enableTracking);
        saveOrUpdateAttendee(attendee);
        updateSignupEventsCollection(attendee);

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
                .update("name", attendee.getName(), "email", attendee.getEmail(), "phoneNumber", attendee.getPhoneNumber(), "imageUrl", attendee.getImageUrl(), "attendeeEnableTrackingOrNot", attendee.isAttendeeEnableTrackingOrNot())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void updateEventsCollection(Attendee attendee) {
        // Query events collection to find events where the attendee is registered
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String eventId = documentSnapshot.getId();
                        // Check if the attendee exists in the attendees subcollection of the event
                        checkAndUpdateAttendeeInEvent(eventId, attendee);
                    }
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully in all events", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to query events: " + e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAndUpdateAttendeeInEvent(String eventId, Attendee attendee) {
        DocumentReference attendeeRef = db.collection("events").document(eventId)
                .collection("attendees")
                .document(attendee.getId());

        attendeeRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Attendee exists in the subcollection, update the data
                        updateAttendeeInEvent(eventId, attendee);
                    } else {
                        // Attendee does not exist in the subcollection

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to check attendee in event " + eventId + ": " + e.getMessage());
                    // Handle failure
                });
    }

    private void updateAttendeeInEvent(String eventId, Attendee attendee) {
        db.collection("events").document(eventId)
                .collection("attendees")
                .document(attendee.getId())
                .update(
                        "name", attendee.getName(),
                        "email", attendee.getEmail(),
                        "phoneNumber", attendee.getPhoneNumber(),
                        "imageUrl", attendee.getImageUrl()
                )
                .addOnSuccessListener(aVoid -> {
                    // Handle success if needed
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to update profile in event " + eventId + ": " + e.getMessage());
                    // Handle failure
                });
    }
    private void updateSignupEventsCollection(Attendee attendee) {
        // Query events collection to find events where the attendee is registered
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String eventId = documentSnapshot.getId();
                        // Check if the attendee exists in the attendees subcollection of the event
                        checkAndUpdateAttendeeInEventSignup(eventId, attendee);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to query events: " + e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAndUpdateAttendeeInEventSignup(String eventId, Attendee attendee) {
        DocumentReference attendeeRef = db.collection("events").document(eventId)
                .collection("Signed Up")
                .document(attendee.getId());

        attendeeRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Attendee exists in the subcollection, update the data
                        updateAttendeeInEventSignup(eventId, attendee);
                    } else {
                        // Attendee does not exist in the subcollection

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to check attendee in event " + eventId + ": " + e.getMessage());
                    // Handle failure
                });
    }

    private void updateAttendeeInEventSignup(String eventId, Attendee attendee) {
        db.collection("events").document(eventId)
                .collection("Signed Up")
                .document(attendee.getId())
                .update(
                        "name", attendee.getName(),
                        "email", attendee.getEmail(),
                        "phoneNumber", attendee.getPhoneNumber(),
                        "imageUrl", attendee.getImageUrl()
                )
                .addOnSuccessListener(aVoid -> {
                    // Handle success if needed
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to update profile in event " + eventId + ": " + e.getMessage());
                    // Handle failure
                });
    }



    private String generateSHA256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap generateShapeBasedImageFromHash(String hash) {
        // Create a bitmap to draw on
        Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Paint for drawing shapes
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Convert hash to byte array for easier handling
        byte[] bytes = hash.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < bytes.length; i += 4) {
            // Use four bytes for each shape to decide type, position, size, and color
            int type = bytes[i] % 2; // Decide between rectangle (0) or circle (1)
            int posX = (bytes[i + 1] & 0xFF) % 128; // Position X
            int posY = (bytes[i + 2] & 0xFF) % 128; // Position Y
            int size = (bytes[i + 3] & 0x0F) + 10; // Size with a minimum of 10 to ensure visibility

            // Setting paint color based on byte values, ensuring a wide range of colors
            paint.setColor(Color.rgb(bytes[i] & 0xFF, bytes[i + 1] & 0xFF, bytes[i + 2] & 0xFF));

            if (type == 0) {
                // Draw rectangle
                canvas.drawRect(posX, posY, posX + size, posY + size, paint);
            } else {
                // Draw circle
                canvas.drawCircle(posX, posY, size / 2.0f, paint);
            }
        }

        return bitmap;
    }


    private void uploadBitmapToStorage(Bitmap bitmap, String attendeeId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + attendeeId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
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

    private void updateAttendeeTrackingPreferenceAcrossEvents(String attendeeId, boolean enableTracking) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch all events
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Loop through all events
                for (DocumentSnapshot eventDocument : task.getResult()) {
                    // Retrieve the event's name
                    String eventName = eventDocument.getString("name");
                    if (eventName == null || eventName.isEmpty()) {
                        Log.e("UpdateTracking", "Event name is missing for document: " + eventDocument.getId());
                        continue; // Skip this iteration if the name is missing
                    }

                    // Reference to the specific attendee in the current event
                    DocumentReference attendeeRef = db.collection("events")
                            .document(eventName)
                            .collection("attendees")
                            .document(attendeeId);

                    // Update the attendeeEnableTrackingOrNot field for the attendee
                    attendeeRef.update("attendeeEnableTrackingOrNot", enableTracking)
                            .addOnSuccessListener(aVoid -> Log.d("UpdateTracking", "Updated tracking preference for attendee: " + attendeeId + " in event: " + eventName))
                            .addOnFailureListener(e -> Log.e("UpdateTracking", "Error updating tracking preference for attendee: " + attendeeId + " in event: " + eventName, e));
                }
            } else {
                Log.e("FetchEvents", "Error fetching events", task.getException());
            }
        });
    }


}
