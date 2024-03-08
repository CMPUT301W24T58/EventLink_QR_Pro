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

        builder.setView(view)
                .setPositiveButton("OK", (dialog, id) -> {
                    String name = eventNameEditText.getText().toString().trim();
                    String date = eventDateEditText.getText().toString().trim();
                    String time = eventTimeEditText.getText().toString().trim();
                    String location = eventLocationEditText.getText().toString().trim();
                    String description = eventDescriptionEditText.getText().toString().trim();

                    // Simple validation
                    if (name.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || description.isEmpty()) {
                        Log.d("CreateEvent", "All fields must be filled");
                        return; // Optionally, show a message to the user
                    }

                    Event event = new Event(name, date, time, location, description);
                    uploadEvent(name, event);
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
}


