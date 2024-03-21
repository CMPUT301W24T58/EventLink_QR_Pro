package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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


import java.io.FileNotFoundException;
import java.io.InputStream;

public class QRCodeScannerActivity extends AppCompatActivity {

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
}

