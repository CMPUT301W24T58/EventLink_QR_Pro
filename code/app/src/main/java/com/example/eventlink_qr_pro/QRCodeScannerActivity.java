package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class QRCodeScannerActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int REQUEST_IMAGE_PICK = 1;
    private Button uploadImageButton;
    private Button takePictureButton;
    private String qrCodeData; // Attribute to store QR code data
    private  Button backbutton;
    private Attendee attendee;

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
                Toast.makeText(QRCodeScannerActivity.this, "Take Picture clicked", Toast.LENGTH_SHORT).show();
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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK);
    }

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
                        String eventName = qrJson.getString("name"); // Assuming eventName is used for document ID
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
    }


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
                                                // No existing milestone found, so we can create a new one
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
                                                // Milestone for this count already exists, handle accordingly (e.g., log a message)
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

