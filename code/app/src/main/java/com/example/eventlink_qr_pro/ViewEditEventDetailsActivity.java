package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An activity for viewing and editing the details of an event.
 * It allows users to modify the event's name, date, location, and description, and optionally set a limit on the number of attendees.
 * Changes are saved to Firestore upon pressing the update button.
 */
public class ViewEditEventDetailsActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;
    private EditText eventTimeEditText;
    private Button updateButton;
    private Button cancelButton;

    private Button uploadButton;

    private FirebaseFirestore db;
    private String eventName;
    private Switch limitAttendeesSwitch;
    private TextView maximumAttendeesLabel;
    private EditText maximumAttendeesEditText;

    /**
     * Initializes the activity, sets up the user interface, and loads existing event details from Firestore.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_edit_event_details);
        db = FirebaseFirestore.getInstance();


        eventNameEditText = findViewById(R.id.event_name_edit_text);
        eventDateEditText = findViewById(R.id.event_date_edit_text);
        eventLocationEditText = findViewById(R.id.event_location_edit_text);
        eventDescriptionEditText = findViewById(R.id.event_description_edit_text);
        updateButton = findViewById(R.id.update_button);
        cancelButton = findViewById(R.id.cancel_button);
        uploadButton = findViewById(R.id.upload_poster_button);
        limitAttendeesSwitch = findViewById(R.id.limit_attendees_switch);
        maximumAttendeesLabel = findViewById(R.id.maximum_attendees_label);
        maximumAttendeesEditText = findViewById(R.id.maximum_attendees_edit_text);
        eventTimeEditText = findViewById(R.id.event_time_edit_text);


        // Retrieve the event name from the intent
        eventName = getIntent().getStringExtra("eventName");

        if (eventName != null) {
            loadEventData(eventName);
        }

        updateButton.setOnClickListener(view -> {
            saveEventDetails();
            finish();
        });

        cancelButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });

        uploadButton.setOnClickListener(view -> {
            Intent intent = new Intent(ViewEditEventDetailsActivity.this, UploadImage.class);
            intent.putExtra("eventName", eventName);
            startActivity(intent);
        });

        limitAttendeesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            maximumAttendeesEditText.setEnabled(isChecked);
            maximumAttendeesEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                maximumAttendeesEditText.setText(""); // Clear the field when the limit is disabled
            }
        });

    }

    /**
     * Loads the event data from Firestore and populates the UI with the retrieved values.
     * @param eventName The name of the event to load data for. Used as the document ID in Firestore.
     */
    private void loadEventData(String eventName) {
        DocumentReference eventRef = db.collection("events").document(eventName);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                eventNameEditText.setText(documentSnapshot.getString("name"));
                eventDateEditText.setText(documentSnapshot.getString("date"));
                eventTimeEditText.setText(documentSnapshot.getString("time"));
                eventLocationEditText.setText(documentSnapshot.getString("location"));
                eventDescriptionEditText.setText(documentSnapshot.getString("description"));

                Number maxAttendees = documentSnapshot.getLong("maxAttendees");
                if (maxAttendees != null) {
                    limitAttendeesSwitch.setChecked(true);
                    maximumAttendeesEditText.setText(maxAttendees.toString());
                    maximumAttendeesEditText.setVisibility(View.VISIBLE);
                    maximumAttendeesEditText.setEnabled(true);
                } else {
                    limitAttendeesSwitch.setChecked(false);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle the error
        });
    }

    /**
     * Saves the user-edited event details back to Firestore. If the limit on attendees is enabled, it includes this value; otherwise, it removes any existing limit.
     */
    private void saveEventDetails() {
        String name = eventNameEditText.getText().toString();
        String date = eventDateEditText.getText().toString();
        String time = eventTimeEditText.getText().toString();
        String location = eventLocationEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();

        // Create a map to store the user-edited event details
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("name", name);
        eventDetails.put("date", date);
        eventDetails.put("time", time);
        eventDetails.put("location", location);
        eventDetails.put("description", description);

        if (limitAttendeesSwitch.isChecked()) {
            String maxAttendeesStr = maximumAttendeesEditText.getText().toString();
            if (!maxAttendeesStr.isEmpty()) {
                int maxAttendees = Integer.parseInt(maxAttendeesStr);
                eventDetails.put("maxAttendees", maxAttendees);
            }
        } else {
            eventDetails.put("maxAttendees", null); // Remove the limit
        }

        JSONObject qrDataJson = new JSONObject();
        try {
            qrDataJson.put("name", eventName);
            qrDataJson.put("date", date);
            qrDataJson.put("time", time);
            qrDataJson.put("location", location);
            qrDataJson.put("description", description);
            // Optionally, include other relevant event details
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Convert JSON to string for uploading
        String qrDataString = qrDataJson.toString();

        // Add QR data string to the event details map
        eventDetails.put("checkinqrdata", qrDataString);

        DocumentReference eventDocRef = db.collection("events").document(eventName);
        eventDocRef.update(eventDetails)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the details
                })
                .addOnFailureListener(e -> {
                    // Failed to update the details
                });
    }


}
