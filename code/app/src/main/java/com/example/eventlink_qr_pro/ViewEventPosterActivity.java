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
 * An activity to display the poster of an event.
 */
public class ViewEventPosterActivity extends AppCompatActivity {

    private ImageView eventPosterImageView;
    private Button backButton;

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
