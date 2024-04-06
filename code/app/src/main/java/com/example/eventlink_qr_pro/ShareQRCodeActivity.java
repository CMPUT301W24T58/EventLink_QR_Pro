package com.example.eventlink_qr_pro;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * An activity that displays a QR code image and provides functionality to share it.
 * The QR code bitmap is passed to this activity through an intent. The activity displays the QR code and
 * allows the user to share it via other apps by creating a temporary file in the cache and sharing its URI.
 */
public class ShareQRCodeActivity extends AppCompatActivity {
    private ImageView qrCodeImageView;
    private Bitmap qrCodeBitmap;
    private Button back;

    /**
     * Initializes the activity by setting the content view, retrieving the QR code bitmap from the intent,
     * and setting up UI components. If the QR code bitmap is missing, it shows an error message and exits the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_qr_code);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        back = findViewById(R.id.back);
        // Retrieve QR code bitmap from intent
        qrCodeBitmap = getIntent().getParcelableExtra("qrCodeBitmap");

        // Check if the bitmap is not null before setting it to the ImageView
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap); // Set the QR code bitmap to the ImageView
        } else {
            // If bitmap is null, display an error message
            Toast.makeText(this, "Error: QR code bitmap not found", Toast.LENGTH_SHORT).show();
            // Finish the activity as the QR code bitmap is essential for sharing
            finish();
        }

        // Setup the share button
        Button btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCodeImage(); // Call the method to share the QR code image
            }
        });
        back.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Shares the QR code image by creating a temporary file in the cache and sharing its URI through an intent.
     * If the image cannot be saved or shared, it displays an error message.
     */
    private void shareQRCodeImage() {
        try {
            // Save bitmap to cache
            Uri imageUri = saveBitmapToCache(qrCodeBitmap);

            if (imageUri != null) {
                // Create share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            } else {
                Toast.makeText(this, "Error while saving QR code image", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.d("QR", e.toString());
            Toast.makeText(this, "Error while sharing QR code image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the given bitmap to the device's external storage cache directory, making it accessible for sharing.
     *
     * @param bitmap The bitmap image to save.
     * @return The URI of the saved image if successful, null otherwise.
     * @throws IOException If an error occurs during saving the image.
     */
    private Uri saveBitmapToCache(Bitmap bitmap) throws IOException {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "QR Code");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        assert uri != null;

        OutputStream out = getContentResolver().openOutputStream(uri);
        boolean saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, Objects.requireNonNull(out));
        out.close();

        return (saved ? uri : null);
    }
}