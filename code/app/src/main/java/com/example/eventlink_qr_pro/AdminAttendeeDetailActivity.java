package com.example.eventlink_qr_pro;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;

/**
 * Activity for displaying and managing details of an event attendee within the admin context. It allows
 * administrators to view and delete the profile image of an attendee. The activity fetches and displays
 * the attendee's image from a provided URL and updates Firestore documents upon deletion of the image.
 */
public class AdminAttendeeDetailActivity extends AppCompatActivity {

    /**
     * Initializes the activity, its views, and sets up the functionality for buttons. It retrieves
     * the image URL, attendee ID, and event name passed through intent extras and sets the image
     * in the ImageView. If no image URL is provided, a default placeholder image is displayed.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_attendee_detail);

        ImageView imageView = findViewById(R.id.attendeeProfileImageView);
        Button btn_back = findViewById(R.id.back);
        Button btnDeleteImage = findViewById(R.id.deleteProfileImageButton);

        // Retrieve the imageUrl passed from the EventDetailAdmin activity
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        String attendeeId = getIntent().getStringExtra("ATTENDEE_ID");
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            new DownloadImageTask(imageView).execute(imageUrl);
        } else {
            imageView.setImageResource(R.drawable.default_placeholder); // Set default placeholder image
        }

        btn_back.setOnClickListener(view -> {
            finish();
        });
        btnDeleteImage.setOnClickListener(view -> deleteAttendeeImage(attendeeId, eventName, imageView));

    }

    /**
     * Deletes the image URL of an attendee from Firestore documents. It updates the UI to display
     * a placeholder image after successful deletion. This method handles Firestore operations for both
     * the specific event's attendee document and the global attendee document.
     *
     * @param attendeeId The ID of the attendee whose image URL is to be deleted.
     * @param eventName The name of the event associated with the attendee.
     * @param imageView The ImageView where the attendee's profile image is displayed.
     */
    private void deleteAttendeeImage(String attendeeId, String eventName, ImageView imageView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Function to delete imageUrl and handle UI update
        Runnable updateUIAfterDeletion = () -> imageView.setImageResource(R.drawable.default_placeholder);

        // Delete imageUrl field from the event's attendee document
        db.collection("events").document(eventName).collection("attendees").document(attendeeId)
                .update("imageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminAttendeeDetail", "Event attendee image url successfully deleted");
                    updateUIAfterDeletion.run();
                })
                .addOnFailureListener(e -> Log.e("AdminAttendeeDetail", "Error deleting event attendee image url", e));

        // Delete imageUrl field from the global attendee document
        db.collection("attendees").document(attendeeId)
                .update("imageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminAttendeeDetail", "Global attendee image url successfully deleted");
                    updateUIAfterDeletion.run();
                })
                .addOnFailureListener(e -> Log.e("AdminAttendeeDetail", "Error deleting global attendee image url", e));
    }

    /**
     * An asynchronous task that downloads an image from a URL and sets it on an ImageView. This task
     * ensures that image downloading does not block the main UI thread.
     */
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
         * Downloads an image from the provided URL in the background.
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
                Log.e("DownloadImageTask", e.getMessage());
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

