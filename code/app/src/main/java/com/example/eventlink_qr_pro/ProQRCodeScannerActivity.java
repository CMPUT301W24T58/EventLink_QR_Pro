package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.InputStream;

/**
 * An activity that allows users to decode QR codes from images. It provides functionality to select an image
 * from the device's storage and decode any QR codes it contains. If a QR code is successfully decoded, the content
 * is displayed in another activity.
 */
public class ProQRCodeScannerActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int IMAGE_PICKER_REQUEST = 1;
    private Button backbutton, uploadImageButton, takePictureButton;
    private Attendee attendee;
    /**
     * Sets up the activity's user interface. Initializes UI components and sets click listeners
     * for the back and upload buttons. The upload button opens the image picker to select an image
     * for QR code decoding.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It's null the first time the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_code_attendee);
        Intent intent = getIntent();
        this.attendee = (Attendee) intent.getSerializableExtra("attendee");

        backbutton = findViewById(R.id.back_to_attendee_menu_button);
        uploadImageButton = findViewById(R.id.upload_image_button);
        takePictureButton = findViewById(R.id.take_picture_button);

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

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    /**
     * Handles the result from the image picker. If an image is selected successfully, it attempts
     * to decode a QR code from the image. On success, navigates to another activity to display the decoded content.
     * On failure, displays a toast message indicating that the QR code could not be decoded.
     *
     * @param requestCode The request code passed in startActivityForResult().
     * @param resultCode The result code returned by the child activity.
     * @param data An Intent that carries the result data.
     */
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                // Use the new decode method that accepts a Bitmap
                String qrCodeContent = decodeQRCode2(imageBitmap);
                if (qrCodeContent != null) {
                    Intent intent = new Intent(this, ShowProQRContent.class);
                    intent.putExtra("qrContent", qrCodeContent);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Failed to decode QR code. Please try again.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error retrieving image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Decodes a QR code from an image URI. Extracts the bitmap from the URI and uses ZXing library to decode the QR code.
     *
     * @param imageUri The URI of the image selected by the user.
     * @return The content encoded in the QR code if successfully decoded; null otherwise.
     */
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

    /**
     * Opens the device's image picker to allow the user to select an image containing a QR code.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICKER_REQUEST);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private String decodeQRCode2(Bitmap bitmap) {
        try {
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            // Get the pixels of the bitmap
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (Exception e) {
            Log.e("DecodeQRCode2", "Error decoding QR code", e);
            return null;
        }
    }

}

