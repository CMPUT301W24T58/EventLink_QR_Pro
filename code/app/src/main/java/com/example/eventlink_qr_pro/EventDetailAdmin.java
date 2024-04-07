package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An administrative activity for viewing detailed information about an event, including its poster,
 * and managing the list of attendees. Allows administrators to delete the event poster and view individual
 * attendee details.
 */
public class EventDetailAdmin extends AppCompatActivity {

    /**
     * Sets up the activity layout, initializes UI components, and fetches event details and attendees from Firestore.
     * It also sets up listeners for the delete and back actions.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It's null the first time the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_admin_image);

        String eventName = getIntent().getStringExtra("EVENT_NAME");
        Button deleteButton = findViewById(R.id.deleteEventPosterButton);
        Button backButton = findViewById(R.id.back);
        deleteButton.setOnClickListener(view -> {
            // Code to delete the event poster
            deleteEventPoster(eventName);
        });
        backButton.setOnClickListener(view -> {
            finish();
        });
        fetchEventDetails(eventName);
    }

    /**
     * Deletes the event poster from the Firestore document of the specified event by setting its imageUrl field to null.
     * Updates the UI to reflect the deletion by displaying a placeholder image.
     *
     * @param eventName The name of the event whose poster is to be deleted.
     */
    private void deleteEventPoster(String eventName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventName)
                .update("imageUrl", null) // Set imageUrl to null to indicate deletion
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventDetailActivity", "Event poster successfully deleted");
                    ImageView imageView = findViewById(R.id.eventPosterImageView);
                    imageView.setImageResource(R.drawable.default_placeholder); // Show a default or placeholder image
                })
                .addOnFailureListener(e -> Log.e("EventDetailActivity", "Error deleting event poster", e));
    }
    /**
     * Fetches and displays the event details from Firestore, including loading the event's poster if an imageUrl exists.
     * Calls {@link #fetchAttendees(String)} to retrieve and display the list of attendees for the event.
     *
     * @param eventName The name of the event for which details are being fetched.
     */
    private void fetchEventDetails(String eventName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventName).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString("imageUrl");
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    new DownloadImageTask((ImageView) findViewById(R.id.eventPosterImageView))
                            .execute(imageUrl);
                } else {
                    // Set the default image if there's no URL
                    ImageView imageView = findViewById(R.id.eventPosterImageView);
                    imageView.setImageResource(R.drawable.default_placeholder);
                }
                fetchAttendees(eventName);
            }
        }).addOnFailureListener(e -> Log.e("EventDetailAdmin", "Error fetching event details", e));
    }

    /**
     * Retrieves the list of attendees for the specified event from Firestore and updates the UI to display the names.
     * Sets up a listener to handle clicks on attendee items, which navigates to a detail view for the selected attendee.
     *
     * @param eventName The name of the event for which attendees are being fetched.
     */
    private void fetchAttendees(String eventName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> attendeeNames = new ArrayList<>();
        List<String> attendeeImageUrls = new ArrayList<>();
        List<String> attendeeIds = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeNames);
        ListView attendeesListView = findViewById(R.id.attendeesListView);
        attendeesListView.setAdapter(adapter);

        // Fetching attendees for the given event
        db.collection("events/" + eventName + "/attendees").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String attendeeName = document.getString("name");
                String imageUrl = document.getString("imageUrl");
                String attendeeId = document.getId();
                if (attendeeName == null || attendeeName.isEmpty()) {
                    attendeeName = "No name provided";
                    String displayText = attendeeName + " (ID: " + attendeeId + ")"; // Combine name and ID
                    attendeeNames.add(displayText);
                    attendeeImageUrls.add(imageUrl);
                    attendeeIds.add(document.getId());
                }else {
                    if (attendeeName != null && !attendeeName.isEmpty()) {
                    attendeeNames.add(attendeeName);
                    attendeeImageUrls.add(imageUrl);
                    attendeeIds.add(document.getId());
                    }
                }

            }

            adapter.notifyDataSetChanged();

            // Making list items clickable
            attendeesListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(EventDetailAdmin.this, AdminAttendeeDetailActivity.class);
                intent.putExtra("IMAGE_URL", attendeeImageUrls.get(position));
                intent.putExtra("ATTENDEE_ID", attendeeIds.get(position)); 
                intent.putExtra("EVENT_NAME", eventName);
                startActivity(intent);
            });




        }).addOnFailureListener(e -> Log.e("EventDetailAdmin", "Error fetching attendees", e));
    }


    /**
     * Asynchronous task for downloading and displaying an image from a given URL.
     * Used to load event posters and attendee profile images from their imageURLs.
     */
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        /**
         * Constructs a new DownloadImageTask associated with a specific ImageView.
         *
         * @param bmImage The ImageView where the downloaded image will be displayed.
         */
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        /**
         * Downloads an image in the background thread from the provided URL.
         *
         * @param urls The URL from which to download the image. Only the first URL is used if multiple are provided.
         * @return The downloaded bitmap image, or null if downloading fails.
         */
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        /**
         * Sets the downloaded image on the ImageView after successful download.
         *
         * @param result The bitmap image downloaded in the background.
         */
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}




