package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView qrCodeImageView;
    private ImageView promoQrCodeImageView; // New ImageView for the promotional QR code
    private String qrDataString;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event); // Make sure this is your correct layout file.

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        promoQrCodeImageView = findViewById(R.id.promoQrCodeImageView);
        TextView textView = findViewById(R.id.event_name_text_view);
        Button btnRegisterQRCode = findViewById(R.id.btn_register_qr_code);
        Button backButton = findViewById(R.id.btn_back);
        Button btnViewEditDetails = findViewById(R.id.btn_view_edit_details);
        Button btnViewAttendees = findViewById(R.id.btn_view_attendees);
        Button btnSendNotification = findViewById(R.id.btn_send_notification);

        eventName = getIntent().getStringExtra("eventName");

        textView.setText(eventName);
        fetchAndGenerateQRCode(eventName);

        btnRegisterQRCode.setOnClickListener(view -> {
            uploadQRCodeToFirestore(eventName, qrDataString);
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

        btnViewEditDetails.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailActivity.this, ViewEditEventDetailsActivity.class);
            intent.putExtra("eventName", eventName); // Optional: Pass data if needed
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
        DocumentReference eventDocRef = db.collection("events").document(eventName);
        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String checkInQrData = documentSnapshot.getString("checkInQrData");
                String promoQrData = documentSnapshot.getString("promoQrData");

                // Generate and display the check-in QR code if data is available
                if (checkInQrData != null && !checkInQrData.isEmpty()) {
                    generateQRCode(checkInQrData, qrCodeImageView);
                }

                // Generate and display the promotional QR code if data is available
                if (promoQrData != null && !promoQrData.isEmpty()) {
                    generateQRCode(promoQrData, promoQrCodeImageView);
                }
            }
        }).addOnFailureListener(e -> {
            // Log or handle the failure to fetch event data
            Log.e("EventDetailActivity", "Error fetching event data", e);
        });
    }


    private String getQrDataString(DocumentSnapshot documentSnapshot) {
        try {
            JSONObject qrDataJson = new JSONObject();
            qrDataJson.put("name", documentSnapshot.getString("name"));
            qrDataJson.put("date", documentSnapshot.getString("date"));
            qrDataJson.put("time", documentSnapshot.getString("time"));
            qrDataJson.put("location", documentSnapshot.getString("location"));
            qrDataJson.put("description", documentSnapshot.getString("description"));
            return qrDataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void generateQRCode(String data, ImageView imageView) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 300, 300);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e("EventDetailActivity", "Error generating QR code", e);
        }
    }

    private String generatePromotionalQrData(String description, String posterUrl) {
        try {
            JSONObject promoQrDataJson = new JSONObject();
            promoQrDataJson.put("description", description);
            promoQrDataJson.put("posterUrl", posterUrl);
            return promoQrDataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void uploadQRCodeToFirestore(String eventName, String qrData) {
        Map<String, Object> qrDataMap = new HashMap<>();
        qrDataMap.put("qrData", qrData);

        db.collection("events").document(eventName)
                .set(qrDataMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
