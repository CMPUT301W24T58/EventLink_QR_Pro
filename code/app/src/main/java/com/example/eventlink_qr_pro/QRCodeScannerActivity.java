package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;

/**
 * An activity that enables users to scan QR codes from images for event check-ins.
 * Users can select an image containing a QR code, which is then decoded to extract event information.
 * Based on the decoded data, the activity handles attendee check-in by updating Firestore documents.
 */
public class QRCodeScannerActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    private Button uploadImageButton;
    private Button takePictureButton;
    private String qrCodeData; // Attribute to store QR code data
    private  Button backbutton;
    private Attendee attendee;

    /**
     * Initializes the activity. Sets up UI components and defines click listeners for the buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_code_attendee);
        Intent intent = getIntent();
        this.attendee = (Attendee) intent.getSerializableExtra("attendee");

        uploadImageButton = findViewById(R.id.upload_image_button);
        takePictureButton = findViewById(R.id.take_picture_button);
        backbutton = findViewById(R.id.back_to_attendee_menu_button);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current AttendeeActivity and return to the previous menu
                finish();
            }
        });
    }

    /**
     * Opens the device's image picker allowing the user to select an image containing a QR code.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK);

    }
    private void dispatchTakePictureIntent() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with capturing image
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                // Camera permission denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles the result from the image picker. Decodes the selected image to extract QR code data,
     * then processes the QR code data for further action like event check-in.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                qrCodeData = decodeQRCode(bitmap); // Store decoded QR code data

                if (qrCodeData != null) {
                    try {
                        JSONObject qrJson = new JSONObject(qrCodeData);
                        String eventName = qrJson.getString("name");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference eventRef = db.collection("events").document(eventName);

                        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    DocumentSnapshot eventDocument = task.getResult();
                                    Number maxAttendees = eventDocument.getLong("maxAttendees");

                                    eventRef.collection("attendees").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> attendeesTask) {
                                            if (attendeesTask.isSuccessful()) {
                                                int currentAttendeesCount = attendeesTask.getResult().size();
                                                if (maxAttendees == null || currentAttendeesCount < maxAttendees.intValue()) {
                                                    // Capacity check passed, proceed to sign in the attendee
                                                    signInAttendee(requestCode, resultCode, data);
                                                } else {
                                                    Toast.makeText(QRCodeScannerActivity.this, "The event has reached its capacity.", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(QRCodeScannerActivity.this, "Failed to check current number of attendees.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(QRCodeScannerActivity.this, "Event not found with provided QR code data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to parse QR code data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Failed to decode QR code", Toast.LENGTH_SHORT).show();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            qrCodeData = decodeQRCode(imageBitmap);

            if (qrCodeData != null) {
                try {
                    JSONObject qrJson = new JSONObject(qrCodeData);
                    String eventName = qrJson.getString("name");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference eventRef = db.collection("events").document(eventName);

                    eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                DocumentSnapshot eventDocument = task.getResult();
                                Number maxAttendees = eventDocument.getLong("maxAttendees");

                                eventRef.collection("attendees").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> attendeesTask) {
                                        if (attendeesTask.isSuccessful()) {
                                            int currentAttendeesCount = attendeesTask.getResult().size();
                                            if (maxAttendees == null || currentAttendeesCount < maxAttendees.intValue()) {
                                                // Capacity check passed, proceed to sign in the attendee
                                                signInAttendee(requestCode, resultCode, data);
                                            } else {
                                                Toast.makeText(QRCodeScannerActivity.this, "The event has reached its capacity.", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(QRCodeScannerActivity.this, "Failed to check current number of attendees.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(QRCodeScannerActivity.this, "Event not found with provided QR code data.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(this, "Failed to parse QR code data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Failed to decode QR code", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Decodes the QR code contained within a Bitmap image and returns the encoded data as a string.
     *
     * @param bitmap The Bitmap image containing the QR code to decode.
     * @return The string representation of the QR code's data, or null if decoding fails.
     */
    private String decodeQRCode(Bitmap bitmap) {
        try {
            int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            Reader reader = new QRCodeReader();
            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Setups up a Firestore snapshot listener for the specified event to track attendee check-ins and milestones.
     *
     * @param eventName The name of the event for which to listen for attendee updates.
     */
    private void setupAttendeeListener(String eventName) {
        db.collection("events").document(eventName).collection("attendees")
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(QRCodeScannerActivity.this, "Error listening to event updates.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        int currentCount = snapshots.size();

                        // Only proceed if the attendee count is a multiple of 5
                        if (currentCount % 5 == 0) {
                            db.collection("events").document(eventName).collection("milestones")
                                    .whereEqualTo("count", currentCount)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Check if the query returned any documents
                                            if (task.getResult().isEmpty()) {

                                                DocumentSnapshot lastAttendee = snapshots.getDocuments().get(snapshots.size() - 1);
                                                com.google.firebase.Timestamp timestamp = lastAttendee.getTimestamp("timestamp");
                                                if (timestamp == null) {
                                                    timestamp = com.google.firebase.Timestamp.now(); // Set to current time if null
                                                }

                                                Map<String, Object> milestone = new HashMap<>();
                                                milestone.put("count", currentCount);
                                                milestone.put("timestamp", timestamp);

                                                db.collection("events").document(eventName).collection("milestones")
                                                        .add(milestone)
                                                        .addOnFailureListener(e -> Toast.makeText(QRCodeScannerActivity.this, "Error saving milestone.", Toast.LENGTH_SHORT).show());
                                            } else {

                                                Log.d("OrganizerAlerts", "Milestone for " + currentCount + " attendees already exists.");
                                            }
                                        } else {
                                            Log.e("OrganizerAlerts", "Error querying for existing milestones", task.getException());
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Signs in an attendee based on the QR code data extracted from an image. It validates the event existence
     * and checks capacity before marking the attendee as checked-in or reporting an error.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned from the started activity.
     * @param data        Additional data from the activity result.
     */
    private void signInAttendee(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                qrCodeData = decodeQRCode(bitmap); // Store decoded QR code data

                if (qrCodeData != null) {
                    try {
                        JSONObject qrJson = new JSONObject(qrCodeData);
                        String eventName = qrJson.getString("name"); // Extract the event name
                        setupAttendeeListener(eventName);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to parse QR code data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Failed to decode QR code", Toast.LENGTH_SHORT).show();
                }

                attendee.find_location(getApplicationContext());
                attendee.getFMCToken();
                if (qrCodeData != null) {
                    // Query Firestore to find the event with the matching qrCodeData
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("events")
                            .whereEqualTo("checkinqrdata", qrCodeData)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        // Get the ID of the event document
                                        String eventId = document.getId();

                                        DocumentReference attendeeRef = db.collection("events").document(eventId)
                                                .collection("attendees").document(attendee.getId());

                                        attendeeRef.get().addOnCompleteListener(attendeeTask -> {
                                            if (attendeeTask.isSuccessful() && attendeeTask.getResult() != null) {
                                                DocumentSnapshot attendeeDocument = attendeeTask.getResult();
                                                if (attendeeDocument.exists()) {
                                                    // Document exists, increment check-in count
                                                    long currentCheckInCount = attendeeDocument.getLong("checkInCount") != null ? attendeeDocument.getLong("checkInCount") : 0;
                                                    attendeeRef.update("checkInCount", currentCheckInCount + 1);
                                                } else {
                                                    // Document does not exist, create it with check-in count set to 1
                                                    attendee.setCheckInCount(1);
                                                    attendeeRef.set(attendee);
                                                    attendeeRef.update("timestamp", FieldValue.serverTimestamp());
                                                }
                                            } else {
                                                // Handle errors or document does not exist scenarios
                                                Toast.makeText(QRCodeScannerActivity.this, "Error fetching attendee data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(QRCodeScannerActivity.this, "Failed to find event with matching QR code data", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Failed to decode QR code", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }


}

