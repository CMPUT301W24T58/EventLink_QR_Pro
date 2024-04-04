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
 * Adapted from Cejiro's EditProfileActivity.java class
 * Similar layout but without the edit functionality, and with a delete functionality instead
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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}

