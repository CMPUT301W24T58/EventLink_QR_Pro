package com.example.eventlink_qr_pro;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrowseDeleteEventAdminDetail extends AppCompatActivity {

    private TextView EventName, eventDate, eventTime, eventLocation, eventDescription;
    private Button btnCancel, btnDelete;
    private ImageView eventPoster;
    private int defaultImageResource = R.drawable.default_placeholder;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_fulldetail_admin);

        EventName = findViewById(R.id.event_name);
        eventDate = findViewById(R.id.event_date);
        eventTime = findViewById(R.id.event_time);
        eventLocation = findViewById(R.id.event_location);
        eventDescription = findViewById(R.id.event_description);
        btnCancel = findViewById(R.id.btn_cancel);
        btnDelete = findViewById(R.id.btn_delete);
        eventPoster = findViewById(R.id.event_poster);

        String eventNameStr = getIntent().getStringExtra("EVENT_NAME");

        fetchEventDetails(eventNameStr);

        btnCancel.setOnClickListener(view -> finish());
        btnDelete.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        if (eventNameStr != null) {
                            deleteEvent(eventNameStr);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });



    }

    private void fetchEventDetails(String eventNameStr) {
        if (eventNameStr == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventNameStr).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Assuming you have fields named exactly like this in your Firestore document
                EventName.setText(documentSnapshot.getString("name"));
                eventDate.setText("Date: " + documentSnapshot.getString("date"));
                eventTime.setText("Time: " + documentSnapshot.getString("time"));
                eventLocation.setText("Location: " + documentSnapshot.getString("location"));
                eventDescription.setText("Description: " + documentSnapshot.getString("description"));
                String imageUrl = documentSnapshot.getString("imageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    loadImageFromUrl(imageUrl); // Load the image
                } else {
                    eventPoster.setImageResource(defaultImageResource); // Use default placeholder
                }
            } else {
                // Handle the case where the document doesn't exist
                EventName.setText("Event not found");
                // Hide or disable delete button if needed
                btnDelete.setEnabled(false);
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });
    }
    private void loadImageFromUrl(String url) {
        executorService.execute(() -> {
            try {
                InputStream in = new URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                runOnUiThread(() -> eventPoster.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> eventPoster.setImageResource(defaultImageResource));
            }
        });
    }
    private void deleteEvent(String eventNameStr) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventNameStr)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Document successfully deleted
                    Log.d("DeleteEvent", "DocumentSnapshot successfully deleted!");
                    Toast.makeText(BrowseDeleteEventAdminDetail.this, "Event successfully deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Close the current activity
                })
                .addOnFailureListener(e -> {
                    // Error deleting document
                    Log.w("DeleteEvent", "Error deleting document", e);
                    Toast.makeText(BrowseDeleteEventAdminDetail.this, "Error deleting event", Toast.LENGTH_SHORT).show();
                });
    }

}
