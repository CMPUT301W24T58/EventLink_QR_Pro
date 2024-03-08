package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView qrCodeImageView, promoQrCodeImageView; // Added for the promotional QR code
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        promoQrCodeImageView = findViewById(R.id.promoQrCodeImageView); // Ensure this ID exists in your layout
        TextView textView = findViewById(R.id.event_name_text_view);
        Button btnRegisterQRCode = findViewById(R.id.btn_register_qr_code);
        Button backButton = findViewById(R.id.btn_back);
        Button btnViewEditDetails = findViewById(R.id.btn_view_edit_details);
        Button btnViewAttendees = findViewById(R.id.btn_view_attendees);
        Button btnSendNotification = findViewById(R.id.btn_send_notification);

        eventName = getIntent().getStringExtra("eventName");

        fetchAndGenerateQRCode(eventName);

        textView.setText(eventName);

        btnViewEditDetails.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailActivity.this, ViewEditEventDetailsActivity.class);
            intent.putExtra("eventName", eventName);
            startActivity(intent);
        });

        btnRegisterQRCode.setOnClickListener(view -> {
            uploadQRCodeToFirestore(eventName);
            fetchAndGenerateQRCode(eventName);
        });

        btnSendNotification.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailActivity.this, SendNotificationActivity.class);
            startActivity(intent);
        });

        btnViewAttendees.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailActivity.this, AttendeeList.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, EventListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndGenerateQRCode(eventName);
    }

    private void fetchAndGenerateQRCode(String eventName) {
        db.collection("events").document(eventName).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String date = documentSnapshot.getString("date");
                String time = documentSnapshot.getString("time");
                String location = documentSnapshot.getString("location");
                String description = documentSnapshot.getString("description");
                String imageUrl = documentSnapshot.contains("imageUrl") ? documentSnapshot.getString("imageUrl") : ""; // Check if imageUrl exists

                JSONObject qrDataJson = new JSONObject();
                try {
                    qrDataJson.put("name", name);
                    qrDataJson.put("date", date);
                    qrDataJson.put("time", time);
                    qrDataJson.put("location", location);
                    // Generate the first QR code with event details
                    generateQRCode(qrDataJson.toString(), qrCodeImageView);

                    // Generate the second QR code with the event description
                    JSONObject promoQrDataJson = new JSONObject();
                    promoQrDataJson.put("description", description);
                    // Include the image URL in the QR code only if it exists
                    if (!imageUrl.isEmpty()) {
                        promoQrDataJson.put("imageUri", imageUrl);
                    }
                    // Always generate the promotional QR code, even if there is no image URL yet
                    generateQRCode(promoQrDataJson.toString(), promoQrCodeImageView);

                } catch (JSONException | WriterException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(e -> {
            // Handle failure
        });
    }

    private void generateQRCode(String data, ImageView targetImageView) throws WriterException {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 300, 300);
        runOnUiThread(() -> targetImageView.setImageBitmap(bitmap));
    }


    private void uploadQRCodeToFirestore(String eventName) {
        // Assuming you want to upload both QR codes' data, you might store them in different fields
        db.collection("events").document(eventName).get().addOnSuccessListener(documentSnapshot -> {
            try {
                // You could potentially update the document with QR code data here if necessary
                // For example, if you generate a new QR code string that includes a timestamp or other dynamic data
                // DocumentReference eventDocRef = db.collection("events").document(eventName);
                // Map<String, Object> qrDataMap = new HashMap<>();
                // qrDataMap.put("qrData", qrDataString); // Your existing QR data
                // Optional: add additional QR data fields as needed
                // eventDocRef.set(qrDataMap, SetOptions.merge());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

