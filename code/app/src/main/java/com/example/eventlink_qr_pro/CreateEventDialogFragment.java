package com.example.eventlink_qr_pro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateEventDialogFragment extends DialogFragment {
    private String qrDataString;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_create_event_dialog, null);


        // Initialize EditText fields
        final EditText eventNameEditText = view.findViewById(R.id.eventName);
        final EditText eventDateEditText = view.findViewById(R.id.eventDate);
        final EditText eventTimeEditText = view.findViewById(R.id.eventTime);
        final EditText eventLocationEditText = view.findViewById(R.id.eventLocation);
        final EditText eventDescriptionEditText = view.findViewById(R.id.eventDescription);
        final Switch switchGeolocation = view.findViewById(R.id.switchGeolocation);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());

        eventDateEditText.setText(currentDate);
        eventTimeEditText.setText(currentTime);

        builder.setView(view)
                .setPositiveButton("OK", (dialog, id) -> {
                    String name = eventNameEditText.getText().toString().trim();
                    String date = eventDateEditText.getText().toString().trim();
                    String time = eventTimeEditText.getText().toString().trim();
                    String location = eventLocationEditText.getText().toString().trim();
                    String description = eventDescriptionEditText.getText().toString().trim();
                    boolean geolocationEnabled = switchGeolocation.isChecked();

                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.w("FCMToken", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }
                                // Once the event is created and we have the token
                                String token = task.getResult();
                                // Save the token to the event document
                                saveTokenToEvent(name, token);
                            });

                    // Simple validation
                    if (name.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || description.isEmpty()) {
                        Log.d("CreateEvent", "All fields must be filled");
                        return; // Optionally, show a message to the user
                    }

                    Event event = new Event(name, date, time, location, description, geolocationEnabled);
                    uploadEvent(name, event);

                    JSONObject qrDataJson = new JSONObject();
                    try {
                        qrDataJson.put("name", name);
                        qrDataJson.put("date", date);
                        qrDataJson.put("time", time);
                        qrDataJson.put("location", location);
                        qrDataJson.put("description", description);
                        qrDataString = qrDataJson.toString();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    uploadQRCodeToFirestore(name,qrDataString);

                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }

    private void uploadEvent(String documentId, Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(documentId) // Consider generating a unique ID here if names can collide
                .set(event)
                .addOnSuccessListener(aVoid -> Log.d("CreateEvent", "Event successfully written!"))
                .addOnFailureListener(e -> Log.w("CreateEvent", "Error adding event", e));

    }

    private void uploadQRCodeToFirestore(String eventName, String qrData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the event document
        DocumentReference eventDocRef = db.collection("events").document(eventName);

        // Map to hold the QR data
        Map<String, Object> qrDataMap = new HashMap<>();
        qrDataMap.put("checkinqrdata", qrData);

        // Merging the QR data into the existing document
        eventDocRef.set(qrDataMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("QRCodeUpload", "QR code data successfully merged into document!");
                })
                .addOnFailureListener(e -> {
                    Log.w("QRCodeUpload", "Error merging QR code data into document", e);
                });
    }

    private void saveTokenToEvent(String eventId, String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.update("organizerToken", token)
                .addOnSuccessListener(aVoid -> Log.d("SaveToken", "Token updated successfully"))
                .addOnFailureListener(e -> Log.e("SaveToken", "Error updating token", e));
    }

}


