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
    private ImageView qrCodeImageView;
    private String qrDataString;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        TextView textView = findViewById(R.id.event_name_text_view);
        Button btnRegisterQRCode = findViewById(R.id.btn_register_qr_code);
        Button backButton = findViewById(R.id.btn_back);
        Button btnViewEditDetails = findViewById(R.id.btn_view_edit_details);
        Button btnViewAttendees = findViewById(R.id.btn_view_attendees);
        Button btnSendNotification = findViewById(R.id.btn_send_notification);

        eventName = getIntent().getStringExtra("eventName");

        db.collection("events").document(eventName).get().addOnSuccessListener(documentSnapshot -> {
            try {
                JSONObject qrDataJson = new JSONObject();
                qrDataJson.put("name", documentSnapshot.getString("name"));
                qrDataJson.put("date", documentSnapshot.getString("date"));
                qrDataJson.put("time", documentSnapshot.getString("time"));
                qrDataJson.put("location", documentSnapshot.getString("location"));
                qrDataJson.put("description", documentSnapshot.getString("description"));
                qrDataString = qrDataJson.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> e.printStackTrace());
        btnViewEditDetails.setOnClickListener(view -> {
            // Create an Intent to start ViewEditEventDetailsActivity
            Intent intent = new Intent(EventDetailActivity.this, ViewEditEventDetailsActivity.class);
            intent.putExtra("eventName", eventName); // Optional: Pass data if needed
            startActivity(intent);

        });

        textView.setText(eventName);


        fetchAndGenerateQRCode(eventName);

        btnRegisterQRCode.setOnClickListener(view -> {
                uploadQRCodeToFirestore(eventName, qrDataString);
                fetchAndGenerateQRCode(eventName);
        });

        btnSendNotification.setOnClickListener(view -> {
            // Create an Intent to start SendNotificationActivity
            Intent intent = new Intent(EventDetailActivity.this, SendNotificationActivity.class);
            startActivity(intent);
        });

        btnViewAttendees.setOnClickListener(view -> {
            // Create an Intent to start SendNotificationActivity
            Intent intent = new Intent(EventDetailActivity.this, AttendeeList.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {
            // Intent to start EventListActivity
            Intent intent = new Intent(this, EventListActivity.class);
            startActivity(intent);
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data each time the activity resumes
        fetchAndGenerateQRCode(eventName);
    }

    private void fetchAndGenerateQRCode(String eventName) {
        db.collection("events").document(eventName).get().addOnSuccessListener(documentSnapshot -> {
            try {
                JSONObject qrDataJson = new JSONObject();
                qrDataJson.put("name", documentSnapshot.getString("name"));
                qrDataJson.put("date", documentSnapshot.getString("date"));
                qrDataJson.put("time", documentSnapshot.getString("time"));
                qrDataJson.put("location", documentSnapshot.getString("location"));
                qrDataJson.put("description", documentSnapshot.getString("description"));
                qrDataString = qrDataJson.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> e.printStackTrace());
        db.collection("events").document(eventName).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String qrData = documentSnapshot.getString("qrData");
                if (qrData != null && !qrData.isEmpty()) {
                    generateQRCode(qrData);
                }else{
                    generateQRCode(qrDataString);
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
    }


    private void generateQRCode(String eventData) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(eventData, BarcodeFormat.QR_CODE, 300, 300);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void uploadQRCodeToFirestore(String eventName, String qrData) {
        DocumentReference eventDocRef = db.collection("events").document(eventName);
        Map<String, Object> qrDataMap = new HashMap<>();
        qrDataMap.put("qrData", qrData);

        eventDocRef.set(qrDataMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

}

