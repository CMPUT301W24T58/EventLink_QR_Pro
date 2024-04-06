package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An activity that displays the content decoded from a professional QR code. This includes showing
 * a description and an image associated with the QR code content, typically representing event details.
 */
public class ShowProQRContent extends AppCompatActivity {

    /**
     * Called when the activity is starting. This method initializes the activity, inflates its UI layout,
     * and sets up the content display based on the QR code data passed via an Intent.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null. This Bundle is not used, as no state is saved/restored in this implementation.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_pro_qr_code);

        Intent intent = getIntent();
        String qrContent = intent.getStringExtra("qrContent");
        Button backButton = findViewById(R.id.back);

        TextView descriptionView = findViewById(R.id.eventDescription);
        ImageView posterView = findViewById(R.id.eventPoster);

        try {
            JSONObject jsonObject = new JSONObject(qrContent);
            String description = jsonObject.optString("description", "Description not available.");
            String posterUrl = jsonObject.optString("posterUrl");

            descriptionView.setText(description);

            // Check if posterUrl is not empty, else set a default placeholder image
            if (!posterUrl.isEmpty()) {
                Glide.with(this).load(posterUrl).into(posterView);
            } else {
                // Load default placeholder image
                Glide.with(this).load(R.drawable.default_placeholder).into(posterView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Glide.with(this).load(R.drawable.default_placeholder).into(posterView);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity, thereby returning to the previous activity
                finish();
            }
        });
    }
}
