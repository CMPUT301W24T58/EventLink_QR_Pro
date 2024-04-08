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

/**
 * An activity that allows users to edit and update their profile information, including their name,
 * email, phone number, and profile picture. The activity also handles uploading the profile image to Firebase Storage
 * and updating the attendee's profile information in Firebase Firestore.
 */
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

    /**
     * Sets up the activity's user interface and initializes Firebase Firestore. Retrieves the attendee
     * object passed from the previous activity to populate the profile fields. Sets listeners for the
     * profile image selection, removal, and profile information update actions.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It is null the first time the activity is created.
     */
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

        String attendeeId = attendee.getId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference attendeeRef = db.collection("attendees").document(attendeeId);

        attendeeRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

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

    /**
     * Initiates an intent to select an image from the device's storage for the profile picture.
     */
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result from the image selection activity, setting the chosen image as the profile picture
     * and preparing it for upload.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached as extras).
     */
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

    /**
     * Saves the updated profile information to Firebase Firestore and uploads the new profile picture
     * to Firebase Storage if one was selected.
     */
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
        System.out.println(enableTracking);
        saveOrUpdateAttendee(attendee);
        updateSignupEventsCollection(attendee);

        // Return updated Attendee object to calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedAttendee", attendee);
        setResult(RESULT_OK, resultIntent);
        finish();

    }

    /**
     * Uploads the selected profile image to Firebase Storage and updates the attendee's profile
     * with the URL of the uploaded image.
     */
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

    /**
     * Updates the attendee document in Firestore with the provided Attendee object's information.
     *
     * @param attendee The Attendee object containing updated profile information.
     */
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
    /**
     * Queries the Firestore database to find all events where the attendee is registered and updates their
     * profile information in each event's attendees subcollection. This ensures that the attendee's
     * profile is consistent across all event registrations.
     *
     * @param attendee The attendee whose profile information is being updated across all events.
     */
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

    /**
     * Checks if the attendee is part of the specified event's attendees subcollection in Firestore.
     * If the attendee is part of the event, their profile information is updated.
     *
     * @param eventId  The ID of the event to check the attendee's registration status.
     * @param attendee The attendee whose presence in the event is being checked and potentially updated.
     */
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

    /**
     * Updates the attendee's profile information in the specified event's attendees subcollection in Firestore.
     * This method is called after confirming the attendee's registration for the event.
     *
     * @param eventId  The ID of the event where the attendee's profile is being updated.
     * @param attendee The attendee whose profile information is being updated.
     */
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
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to update profile in event " + eventId + ": " + e.getMessage());
                    // Handle failure
                });
    }
    /**
     * Queries the Firestore database to find all events where the attendee has signed up for future participation.
     * It then updates the attendee's profile information in each event's "Signed Up" subcollection.
     *
     * @param attendee The attendee whose profile information is being updated in future event sign-ups.
     */
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

    /**
     * Checks if the attendee is part of the specified event's "Signed Up" subcollection in Firestore.
     * If the attendee has signed up for the event, their profile information is updated.
     *
     * @param eventId  The ID of the event to check the attendee's sign-up status.
     * @param attendee The attendee whose sign-up status is being checked and potentially updated.
     */
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

    /**
     * Updates the attendee's profile information in the specified event's "Signed Up" subcollection in Firestore.
     * This method is called after confirming the attendee's future participation in the event.
     *
     * @param eventId  The ID of the event where the attendee's profile information for future participation is being updated.
     * @param attendee The attendee whose profile information for future participation is being updated.
     */
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
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to update profile in event " + eventId + ": " + e.getMessage());
                    // Handle failure
                });
    }


    /**
     * Generates a SHA-256 hash of the provided text. This is used in creating a unique identifier for the attendee.
     *
     * @param text The text to hash.
     * @return A SHA-256 hash of the text.
     */
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

    /**
     * Generates a shape-based image from the provided hash. This method is used when the attendee does not select
     * a profile picture, providing a unique, generated image instead.
     *
     * @param hash The hash from which to generate the image.
     * @return A Bitmap image generated based on the hash.
     */
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


    /**
     * Uploads a generated Bitmap image to Firebase Storage under the attendee's ID and updates
     * the attendee's Firestore document with the URL of the uploaded image.
     *
     * @param bitmap     The Bitmap image to upload.
     * @param attendeeId The ID of the attendee for whom the image is being uploaded.
     */
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

    /**
     * Updates the tracking preference for an attendee across all events they are associated with.
     *
     * @param attendeeId      The ID of the attendee whose tracking preference is to be updated.
     * @param enableTracking  The new tracking preference.
     */
    private void updateAttendeeTrackingPreferenceAcrossEvents(String attendeeId, boolean enableTracking) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch all events
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Loop through all events
                for (DocumentSnapshot eventDocument : task.getResult()) {
                    // Use the document ID as the event ID
                    String eventId = eventDocument.getId();

                    // Reference to the specific attendee in the current event
                    DocumentReference attendeeRef = db.collection("events")
                            .document(eventId) // Use eventId here
                            .collection("attendees")
                            .document(attendeeId);

                    // Update the attendeeEnableTrackingOrNot field for the attendee
                    attendeeRef.update("attendeeEnableTrackingOrNot", enableTracking)
                            .addOnSuccessListener(aVoid -> Log.d("UpdateTracking", "Updated tracking preference for attendee: " + attendeeId + " in event: " + eventId))
                            .addOnFailureListener(e -> Log.e("UpdateTracking", "Error updating tracking preference for attendee: " + attendeeId + " in event: " + eventId, e));
                }
            } else {
                Log.e("FetchEvents", "Error fetching events", task.getException());
            }
        });
    }



}
