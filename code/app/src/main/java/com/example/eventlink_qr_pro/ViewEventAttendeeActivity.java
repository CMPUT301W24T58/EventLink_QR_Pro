package com.example.eventlink_qr_pro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An activity that allows attendees to view event details and sign up for the event.
 * This activity displays information such as the event's name, date, location, and description.
 * It also provides functionality for an attendee to sign up for the chosen event.
 */
public class ViewEventAttendeeActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;
    private Button signupButton;
    private Button cancelButton;
    private Button posterbutton;

    private String qrdata;
    private FirebaseFirestore db;
    private String eventName;
    private Attendee attendee;

    /**
     * Initializes the activity, sets up the user interface, and retrieves event details from Firestore
     * based on the event name passed through an Intent. It also sets up the signup functionality
     * for the attendee to sign up for the event.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_view_event_details);
        db = FirebaseFirestore.getInstance();


        eventNameEditText = findViewById(R.id.event_name_edit_text_attendee);
        eventDateEditText = findViewById(R.id.event_date_edit_text_attendee);
        eventLocationEditText = findViewById(R.id.event_location_edit_text_attendee);
        eventDescriptionEditText = findViewById(R.id.event_description_edit_text_attendee);
        signupButton = findViewById(R.id.update_button_attendee);
        cancelButton = findViewById(R.id.cancel_button_attendee);
        posterbutton = findViewById(R.id.view_poster_button);


        // Retrieve the event name from the intent
        Intent intent = getIntent();


        attendee = (Attendee) intent.getSerializableExtra("attendee");
        eventName = intent.getStringExtra("eventName");

        if (eventName != null) {
            loadEventData(eventName);
        }

        signupButton.setOnClickListener(view -> {
            fetchEventCheckInQRData(eventName, qrData -> {
                this.qrdata = qrData;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("events")
                        .whereEqualTo("checkinqrdata", this.qrdata)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Get the ID of the event document
                                    String eventId = document.getId();

                                    DocumentReference attendeeRef = db.collection("events").document(eventId)
                                            .collection("Signed Up").document(attendee.getId());

                                    attendeeRef.get().addOnCompleteListener(attendeeTask -> {
                                        if (attendeeTask.isSuccessful() && attendeeTask.getResult() != null) {
                                            DocumentSnapshot attendeeDocument = attendeeTask.getResult();
                                            if (attendeeDocument.exists()) {
                                                // Document exists
                                                Toast.makeText(ViewEventAttendeeActivity.this, "Already signed up for this event", Toast.LENGTH_SHORT).show();
                                            } else {

                                                attendeeRef.set(attendee);
                                                Toast.makeText(ViewEventAttendeeActivity.this, "Successfully signed up for this event", Toast.LENGTH_SHORT).show();
                                                finish();

                                            }
                                        } else {
                                            // Handle errors or document does not exist scenarios
                                            Toast.makeText(ViewEventAttendeeActivity.this, "Error fetching attendee data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(ViewEventAttendeeActivity.this, "Failed to find event", Toast.LENGTH_SHORT).show();
                            }
                        });

            });
        });

        cancelButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });

        posterbutton.setOnClickListener(view ->{
            // Check if the event in Firestore has an 'imageUrl' field
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("events").document(eventName);

            eventRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("imageUrl")) {
                    // Event has an 'imageUrl' field, launch the activity to view the event poster
                    Intent intent2 = new Intent(ViewEventAttendeeActivity.this, ViewEventPosterActivity.class);
                    intent2.putExtra("eventName", eventName); // Pass the event name to the poster view activity
                    startActivity(intent2);
                } else {
                    // Event does not have an 'imageUrl' field
                    Toast.makeText(ViewEventAttendeeActivity.this, "Event poster not available", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Handle failure to retrieve document
                Toast.makeText(ViewEventAttendeeActivity.this, "Failed to check event poster availability", Toast.LENGTH_SHORT).show();
            });

        });



    }

    /**
     * Loads the event data from Firestore and populates the UI with the retrieved values.
     *
     * @param eventName The name of the event to load data for, used as the document ID in Firestore.
     */
    private void loadEventData(String eventName) {
        DocumentReference eventRef = db.collection("events").document(eventName);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                eventNameEditText.setText(documentSnapshot.getString("name"));
                eventDateEditText.setText(documentSnapshot.getString("date"));
                eventLocationEditText.setText(documentSnapshot.getString("location"));
                eventDescriptionEditText.setText(documentSnapshot.getString("description"));
            }
        }).addOnFailureListener(e -> {
            // Handle the error
        });
    }
    /**
     * A callback interface to handle the QR data fetched from Firestore.
     */
    private interface QRDataCallback {
        /**
         * Called when QR data is successfully fetched.
         *
         * @param qrData The QR data string fetched from the Firestore document.
         */
        void onCallback(String qrData);
    }
    /**
     * Fetches the 'checkinqrdata' field for a specific event from Firestore and invokes the provided callback with the fetched data.
     * This method asynchronously retrieves the event document based on the provided {@code eventId}. If the document exists and contains
     * the 'checkinqrdata' field, the {@link QRDataCallback#onCallback(String)} method of the provided {@code callback} is called with the
     * fetched QR data string. If the document does not exist or does not contain the 'checkinqrdata' field, appropriate log messages are generated.
     *
     * @param eventId The ID of the event document to fetch from Firestore.
     * @param callback The {@link QRDataCallback} instance to be invoked after fetching the QR data.
     */
    private void fetchEventCheckInQRData(String eventId, QRDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String qrData = documentSnapshot.getString("checkinqrdata");
                if (qrData != null) {
                    Log.d("FetchQRData", "QR Data: " + qrData);
                    callback.onCallback(qrData); // Trigger the callback with the fetched qrData
                } else {
                    Log.d("FetchQRData", "Document does not contain 'checkinqrdata'");
                }
            } else {
                Log.d("FetchQRData", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("FetchQRData", "Failed to fetch document", e));
    }


}

