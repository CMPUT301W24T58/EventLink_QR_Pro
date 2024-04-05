package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.InputStream;

public class ProQRCodeScannerActivity extends AppCompatActivity {
    private static final int IMAGE_PICKER_REQUEST = 1;
    private Button backbutton, uploadImageButton;
    private Attendee attendee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_code_attendee);
        Intent intent = getIntent();
        this.attendee = (Attendee) intent.getSerializableExtra("attendee");

        backbutton = findViewById(R.id.back_to_attendee_menu_button);
        uploadImageButton = findViewById(R.id.upload_image_button);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current AttendeeActivity and return to the previous menu
                finish();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker
                openImagePicker();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // Decode the QR code from the selected image
            String qrCodeContent = decodeQRCode(imageUri);
            if (qrCodeContent != null) {
                Intent intent = new Intent(this, ShowProQRContent.class);
                intent.putExtra("qrContent", qrCodeContent);
                startActivity(intent);
            } else {
                // Inform the user the QR code could not be decoded
                Toast.makeText(this, "Failed to decode QR code. Please try a different image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String decodeQRCode(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                Log.e("DecodeQRCode", "Could not decode bitmap");
                return null;
            }
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (Exception e) {
            Log.e("DecodeQRCode", "Error decoding QR code", e);
            return null;
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICKER_REQUEST);
    }
}
