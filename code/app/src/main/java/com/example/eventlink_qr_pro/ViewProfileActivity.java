package com.example.eventlink_qr_pro;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import com.bumptech.glide.Glide;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.Serializable;

/**
 * Activity for viewing an attendee's profile in detail, including their name, email, phone number,
 * and profile image. This activity also provides functionality to delete the attendee's profile.
 */
public class ViewProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private TextView nameTextView, emailTextView, phoneTextView;
    private Button deleteButton, cancelButton;

    private FirebaseFirestore db;
    private Bitmap bitmap;
    private Attendee attendee;
    private String imageUrl;

    /**
     * Initializes the activity, sets up the user interface, and displays the attendee's profile information.
     * It retrieves the Attendee object and image URL from the intent, populates the UI elements with the
     * attendee's information, and sets up listeners for the delete and cancel actions.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        db = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.profPicImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        deleteButton = findViewById(R.id.deleteButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Retrieve Attendee object from intent
        attendee = (Attendee) getIntent().getSerializableExtra("attendee");
        imageUrl = getIntent().getStringExtra("imageUrl");

        // Populate fields with Intent data
        nameTextView.setText("Name: " + attendee.getName());
        emailTextView.setText("Email: " + attendee.getEmail());
        phoneTextView.setText("Phone Number: " + attendee.getPhoneNumber());

        if (imageUrl != null && !imageUrl.isEmpty()) {
            new DownloadImageTask(imageView).execute(imageUrl);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: database delete profile
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("attendees")
                        .document(attendee.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ViewProfileActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                                deleteAttendeeFromEvents(attendee.getId());
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ViewProfileActivity.this, "Profile deletion failed", Toast.LENGTH_SHORT).show();
                                Log.d("Profile Delete", "Profile deletion failed; error: " + e);
                                finish();
                            }
                        });
            }
        }));
    }
    /**
     * Deletes the attendee's profile from the Firestore database. This method also removes the
     * attendee from all events' attendees and Signed Up subcollections. Upon successful deletion,
     * it displays a confirmation message and finishes the activity. In case of failure, it shows
     * an error message.
     *
     * @param attendeeId The unique ID of the attendee to be deleted.
     */
    private void deleteAttendeeFromEvents(String attendeeId) {
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot eventDocument : task.getResult()) {
                        String eventName = eventDocument.getId();
                        db.collection("events").document(eventName).collection("attendees")
                                .whereEqualTo("id", attendeeId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> attendeesTask) {
                                        if (attendeesTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot attendeeDocument : attendeesTask.getResult()) {
                                                // Delete each found attendee document
                                                attendeeDocument.getReference().delete();
                                            }
                                        }
                                    }
                                });
                        db.collection("events").document(eventName).collection("Signed Up")
                                .whereEqualTo("id", attendeeId)
                                .get()
                                .addOnCompleteListener(signedUpTask -> {
                                    if (signedUpTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot signedUpDocument : signedUpTask.getResult()) {
                                            signedUpDocument.getReference().delete()
                                                    .addOnSuccessListener(aVoid -> Log.d("DeleteAttendee", "Successfully deleted attendee from Signed Up in " + eventName))
                                                    .addOnFailureListener(e -> Log.e("DeleteAttendee", "Error deleting attendee from Signed Up in " + eventName, e));
                                        }
                                    }
                                });
                    }
                } else {
                    Log.d("Event Fetch", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Asynchronous task for downloading and displaying an image from a URL. It is used here to load
     * and display the attendee's profile image.
     */
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        /**
         * Constructs an instance of the DownloadImageTask with the specified ImageView.
         *
         * @param bmImage The ImageView where the downloaded image will be displayed.
         */
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        /**
         * Downloads the image from the provided URL in the background. This method runs on a separate thread
         * to prevent blocking the UI thread.
         *
         * @param urls The array of URLs to download the image from. Only the first URL in the array is used.
         * @return The downloaded bitmap image, or {@code null} if the download fails.
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
         * Runs on the UI thread after the background computation finishes. Sets the downloaded image bitmap
         * to the ImageView provided in the constructor.
         *
         * @param result The bitmap image downloaded by doInBackground(String...).
         */
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}

