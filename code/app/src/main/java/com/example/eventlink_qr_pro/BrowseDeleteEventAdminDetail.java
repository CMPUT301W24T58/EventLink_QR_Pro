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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity for displaying detailed information about an event to an admin user, with options to cancel or delete the event.
 * It fetches event details from Firestore based on the event name passed via intent and displays them.
 * Additionally, it handles the deletion of the event with confirmation.
 */
public class BrowseDeleteEventAdminDetail extends AppCompatActivity {

    private TextView EventName, eventDate, eventTime, eventLocation, eventDescription;
    private Button btnCancel, btnDelete;
    private ImageView eventPoster;
    private int defaultImageResource = R.drawable.default_placeholder;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    /**
     * Sets up the activity layout and initializes UI components. It fetches and displays the event details from Firestore.
     * Provides options to cancel viewing the event or to delete the event with a confirmation dialog.
     *
     * @param savedInstanceState Bundle: If the activity is being re-initialized after previously being shut down, this
     *                           Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
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

    /**
     * Fetches the details of the event from Firestore using the event name and updates the UI elements with these details.
     * If the event has an associated image URL, it attempts to load the image asynchronously.
     *
     * @param eventNameStr The name of the event for which details are to be fetched.
     */
    private void fetchEventDetails(String eventNameStr) {
        if (eventNameStr == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventNameStr).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
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
    /**
     * Asynchronously loads an image from a given URL and sets it on the event poster ImageView. If loading fails,
     * sets a default placeholder image.
     *
     * @param url The URL of the image to load.
     */
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
    /**
     * Deletes the event from Firestore after confirmation from the admin. Displays a toast message based on the outcome
     * of the deletion operation.
     *
     * @param eventNameStr The name of the event to be deleted.
     */
    private void deleteEvent(String eventNameStr) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventNameStr)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Document successfully deleted
                    deleteSubCollectionDocuments(eventNameStr, "attendees");
                    deleteSubCollectionDocuments(eventNameStr, "Signed Up");
                    deleteSubCollectionDocuments(eventNameStr, "messages");
                    deleteSubCollectionDocuments(eventNameStr, "milestones");
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
    /**
     * Deletes all documents in a specified subcollection for an event in Firestore.
     *
     * This method asynchronously fetches and deletes each document within the given subcollection of an event.
     * Logs success or error messages for each deletion attempt.
     *
     * @param eventNameStr The name of the event document in the 'events' collection.
     * @param subCollectionName The name of the subcollection to delete documents from.
     */
    private void deleteSubCollectionDocuments(String eventNameStr, String subCollectionName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventNameStr).collection(subCollectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            db.collection("events").document(eventNameStr).collection(subCollectionName).document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("DeleteSubCollection", "Document successfully deleted"))
                                    .addOnFailureListener(e -> Log.w("DeleteSubCollection", "Error deleting document", e));
                        }
                    } else {
                        Log.d("DeleteSubCollection", "Error getting subcollection documents: ", task.getException());
                    }
                });
    }


}
