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

public class ShareQRCodeActivity extends AppCompatActivity {
    private ImageView qrCodeImageView;
    private Bitmap qrCodeBitmap;
    private Button back;

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

    // Method to save the QR code bitmap to cache
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