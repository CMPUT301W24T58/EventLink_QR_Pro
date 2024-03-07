package com.example.eventlink_qr_pro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateEventDialogFragment extends DialogFragment {
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
        final EditText eventPosterUrlEditText = view.findViewById(R.id.eventPosterUrl);

        builder.setView(view)
                .setPositiveButton("OK", (dialog, id) -> {
                    // Validate and upload event
                    createAndUploadEvent(eventNameEditText, eventDateEditText, eventTimeEditText,
                            eventLocationEditText, eventDescriptionEditText,
                            eventPosterUrlEditText);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }

    private void createAndUploadEvent(EditText... editTexts) {
        String name = editTexts[0].getText().toString().trim();
        String date = editTexts[1].getText().toString().trim();
        String time = editTexts[2].getText().toString().trim();
        String location = editTexts[3].getText().toString().trim();
        String description = editTexts[4].getText().toString().trim();
        String posterUrl = editTexts[5].getText().toString().trim();

        // Validation omitted for brevity. You should check for empty fields here.

        // Assuming you have a method to create JSON strings for the QR codes
        String checkInQrDataString = generateCheckInQRDataString(name, date, time, location);
        String promoQrDataString = generatePromoQRDataString(description, posterUrl);

        // Upload event with QR data strings to Firestore
        uploadEvent(name, date, time, location, description, posterUrl,
                checkInQrDataString, promoQrDataString);
    }

    private String generateCheckInQRDataString(String name, String date, String time, String location) {
        // Generate check-in QR data string
        try {
            JSONObject qrDataJson = new JSONObject();
            qrDataJson.put("name", name);
            qrDataJson.put("date", date);
            qrDataJson.put("time", time);
            qrDataJson.put("location", location);
            return qrDataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generatePromoQRDataString(String description, String posterUrl) {
        // Generate promotional QR data string
        try {
            JSONObject qrDataJson = new JSONObject();
            qrDataJson.put("description", description);
            qrDataJson.put("posterUrl", posterUrl);
            return qrDataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadEvent(String name, String date, String time, String location,
                             String description, String posterUrl, String checkInQrDataString,
                             String promoQrDataString) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> event = new HashMap<>();
        event.put("name", name);
        event.put("date", date);
        event.put("time", time);
        event.put("location", location);
        event.put("description", description);
        event.put("posterUrl", posterUrl);
        event.put("checkInQrData", checkInQrDataString);
        event.put("promoQrData", promoQrDataString);

        db.collection("events").document(name) // Using event name as document ID; ensure uniqueness
                .set(event)
                .addOnSuccessListener(aVoid -> Log.d("CreateEvent", "Event successfully written!"))
                .addOnFailureListener(e -> Log.w("CreateEvent", "Error writing event", e));
    }
}