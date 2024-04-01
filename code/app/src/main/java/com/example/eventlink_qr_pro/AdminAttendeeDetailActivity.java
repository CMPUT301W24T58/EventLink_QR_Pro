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

public class AdminAttendeeDetailActivity extends AppCompatActivity {

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

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

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

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

