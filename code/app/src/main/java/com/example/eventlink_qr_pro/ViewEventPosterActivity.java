package com.example.eventlink_qr_pro;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Button;



/**
 * {@code ViewEventPosterActivity} displays the event poster for a specific event.
 * It retrieves the event's name passed via intent, then queries Firestore to get the event's
 * poster image URL. The image is then loaded and displayed using Glide. If the event details
 * are not found or the event does not have a poster, the user is informed via a Toast message.
 * This activity also includes a back button to return to the previous screen.
 */
public class ViewEventPosterActivity extends AppCompatActivity {

    private ImageView eventPosterImageView;
    private Button backButton;
    /**
     * Initializes the activity, setting its content view and finding views by ID.
     * Retrieves the event name from the intent, queries Firestore for the event's poster URL,
     * and uses Glide to load and display the image. If no image URL is found or if the Firestore
     * document does not exist, displays a relevant Toast message. Sets up the back button to
     * finish the activity when clicked.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state, or null if
     *                           this is the first time the activity is created.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_poster_attendee);

        eventPosterImageView = findViewById(R.id.image_view);
        backButton = findViewById(R.id.back_button);

        // Retrieve the event name from the intent
        String eventName = getIntent().getStringExtra("eventName");

        // Get the event poster URL from Firestore and load the image using Picasso
        if (eventName != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("events").document(eventName);
            eventRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("imageUrl")) {
                    String imageUrl = documentSnapshot.getString("imageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(this)
                                .asBitmap()
                                .load(imageUrl)
                                .apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL)) // Set the size of the loaded image
                                .into(new BitmapImageViewTarget(eventPosterImageView) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        // Use the Bitmap resource here
                                        eventPosterImageView.setImageBitmap(resource);

                                    }
                                });
                    } else {
                        // If imageUrl is empty or null, show a toast indicating no poster available
                        Toast.makeText(ViewEventPosterActivity.this, "No poster available for this event", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // If the document doesn't exist or doesn't contain imageUrl, show a toast
                    Toast.makeText(ViewEventPosterActivity.this, "Event details not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Handle failure to retrieve document
                Toast.makeText(ViewEventPosterActivity.this, "Failed to retrieve event details", Toast.LENGTH_SHORT).show();
            });
        }

        backButton.setOnClickListener(view -> {
            finish();
        });
    }


}
