package com.example.eventlink_qr_pro;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
/**
 * An activity that displays a list of attendees to an admin. The list is fetched from Firestore,
 * and each item in the list can be clicked to view detailed information about the attendee.
 * This class handles the retrieval of attendee data from Firestore and initializes the ListView
 * adapter to display this data. It also sets up an item click listener for the ListView to handle
 * navigation to the attendee's profile view.
 */
public class AdminAttendeeList extends AppCompatActivity {
    Button backButton;
    ListView attendeesListView;
    ArrayList<String> attendeeDetails;
    ArrayAdapter<String> arrayAdapter;
    FirebaseFirestore db;

    /**
     * Initializes the activity, including the ListView, ArrayAdapter, and the Firestore instance.
     * It sets up an OnClickListener for the ListView items to navigate to the {@link ViewProfileActivity}
     * with the selected attendee's details.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_list);

        backButton = findViewById(R.id.back);
        attendeesListView = findViewById(R.id.listview_attendees);
        attendeeDetails = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        attendeesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userId = attendeeDetails.get(position).substring(attendeeDetails.get(position).length() - 36);
                Intent intent = new Intent(AdminAttendeeList.this, ViewProfileActivity.class);
                db.collection("attendees")
                        .document(userId)
                        .get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                Log.d("debug", "task faileded");
                            }
                        })
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                String name = task.getResult().getString("name");
                                String email = task.getResult().getString("email");
                                String phone = task.getResult().getString("phone");
                                String imageUrl = task.getResult().getString("imageUrl");
                                Object imageBytes = task.getResult().get("imageByteArray");
                                Attendee attendee = new Attendee(userId, name, email, phone);
                                attendee.setImageByteArray((byte[]) imageBytes);
                                intent.putExtra("attendee", attendee);
                                intent.putExtra("imageUrl", imageUrl);
                                startActivity(intent);
                            }
                        });
            }
        });
        backButton.setOnClickListener(view -> finish());

        setupListViewAdapter();
    }

    /**
     * Sets up the ArrayAdapter for the ListView. This adapter is responsible for converting
     * the attendee details into view items within the ListView.
     */
    private void setupListViewAdapter() {
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeDetails);
        attendeesListView.setAdapter(arrayAdapter);
    }

    /**
     * Refreshes the list of attendees every time the activity is resumed.
     * It ensures the attendee list is always up to date.
     */
    @Override
    protected void onResume() {
        super.onResume();
        adminFetchAttendees(); // Refresh the list every time the activity resumes
    }

    /**
     * Fetches the list of attendees from Firestore and updates the ListView adapter.
     * It clears the current attendee details and refills them with fresh data from Firestore.
     * Upon successful data retrieval, it notifies the ArrayAdapter to refresh the ListView.
     */
    private void adminFetchAttendees() {
        attendeeDetails.clear();
        db.collection("attendees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String id = document.getString("id");
                            String detail = "Name: " + name + ", ID: " + id;
                            attendeeDetails.add(detail);
                        }
                        arrayAdapter.notifyDataSetChanged(); // Notify adapter
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}

