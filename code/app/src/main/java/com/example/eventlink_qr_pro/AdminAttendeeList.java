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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Modified duplicate of AttendeeList.java
 * Used to get the AttendeeList listview contents for the Admin "Browse/remove profiles" functionality
 */

public class AdminAttendeeList extends AppCompatActivity {
    Button backButton;
    ListView attendeesListView;
    ArrayList<String> attendeeDetails;
    ArrayAdapter<String> arrayAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_list);

        backButton = findViewById(R.id.back);
        attendeesListView = findViewById(R.id.listview_attendees);
        attendeeDetails = new ArrayList<>();
        //arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeDetails);
        db = FirebaseFirestore.getInstance();

        backButton.setOnClickListener(view -> {
            // Close this activity and go back to the previous one
            finish();
        });

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
                                    Object imageBytes = task.getResult().get("imageByteArray");
                                    Attendee attendee = new Attendee(userId, name, email, phone);
                                    attendee.setImageByteArray((byte[]) imageBytes);
                                    intent.putExtra("attendee", attendee);
//                                    intent.putExtra("name", name);
//                                    intent.putExtra("email", email);
//                                    intent.putExtra("phone", phone);
//                                    Log.d("USERINFO", "Name: " + name);
//                                    intent.putExtra("ID", userId);
                                    startActivity(intent);
                                }
                        });
            }
        });

        // don't need the event reference for the admin "browse/remove profiles"
        //String eventName = getIntent().getStringExtra("eventName"); // Retrieve the event name passed through the intent
        //fetchAttendees(eventName);
        // modified for admin:
        adminFetchAttendees();
    }

    private void adminFetchAttendees() {
        //attendeeDetails = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendeeDetails);
        attendeesListView.setAdapter(arrayAdapter);

        db.collection("attendees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String id = document.getString("id");
                            Log.d("Readme", id);
                            //String email = document.getString("email");
                            // Now fetch the check-in count from the document
                            //Long checkInCount = document.contains("checkInCount") ? document.getLong("checkInCount") : 0; // Default to 0 if not present
                            String detail = "Name: " + name + ", ID: " + id;
                            attendeeDetails.add(detail);
                        }
                        arrayAdapter.notifyDataSetChanged(); // Update the ListView with the new data
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }}
                });
    }


}
