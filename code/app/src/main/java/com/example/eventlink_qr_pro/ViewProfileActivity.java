package com.example.eventlink_qr_pro;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import com.google.firebase.firestore.FirebaseFirestore;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
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
    private String userId;

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
        //attendee = (Attendee) getIntent().getSerializableExtra("attendee");

        // TODO: Fix this so it works, maybe pass the byte array through the intent?
//        if (attendee.getImageByteArray() != null){
//            Bitmap bitmap = BitmapFactory.decodeByteArray(attendee.getImageByteArray(), 0, attendee.getImageByteArray().length);
//            imageView.setImageBitmap(bitmap);
//        }

        // Populate fields with Intent data
        userId = getIntent().getStringExtra("ID");
//        nameTextView.setText("NAME GOES HERE");
//        emailTextView.setText("EMAIL GOES HERE");
//        phoneTextView.setText("PHONE GOES HERE");
        nameTextView.setText((String) getIntent().getStringExtra("name"));
        emailTextView.setText((String) getIntent().getStringExtra("email"));
        phoneTextView.setText((String) getIntent().getStringExtra("phone"));

//        chooseImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });
//
//        removeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView.setImageDrawable(null);
//                attendee.clearImageByteArray();
//            }
//        });
//
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveProfile();
//            }
//        });

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
            }
        }));
    }

//    private void chooseImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            filePath = data.getData();
//            try {
//                this.bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//                attendee.setImageByteArray(byteArray);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void saveProfile() {
//        String name = nameEditText.getText().toString().trim();
//        String email = emailEditText.getText().toString().trim();
//        String phone = phoneEditText.getText().toString().trim();
//
//        if (TextUtils.isEmpty(name)) {
//            name = "";
//        } else if (TextUtils.isEmpty(email)) {
//            email = "";
//
//        } else if (TextUtils.isEmpty(phone)) {
//            phone ="";
//
//        }
//
//        // Update Attendee object with new data
//        attendee.setName(name);
//        attendee.setEmail(email);
//        attendee.setPhoneNumber(phone);
//
//        db.collection("attendees").document(attendee.getId())
//                .update("name", attendee.getName(), "email", attendee.getEmail(), "phone", attendee.getPhoneNumber())
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//
//        // Return updated Attendee object to calling activity
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("updatedAttendee", attendee);
//        setResult(RESULT_OK, resultIntent);
//        finish();
//    }
}

