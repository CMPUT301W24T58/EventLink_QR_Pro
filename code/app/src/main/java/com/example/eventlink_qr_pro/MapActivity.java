package com.example.eventlink_qr_pro;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventName; // Event name passed to this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Retrieve the event name from the intent
        eventName = getIntent().getStringExtra("eventName");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (eventName != null && !eventName.isEmpty()) {
            fetchAttendeesAndMarkThem(eventName);
        }
    }

    private void fetchAttendeesAndMarkThem(String eventName) {
        db.collection("events")
                .whereEqualTo("name", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Fetch the attendees for the first (and supposedly only) event document
                        QueryDocumentSnapshot eventDocument = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        eventDocument.getReference().collection("attendees")
                                .get()
                                .addOnCompleteListener(attendeeTask -> {
                                    if (attendeeTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot attendee : attendeeTask.getResult()) {
                                            if (attendee.contains("latitude") && attendee.contains("longitude")) {
                                                double lat = attendee.getDouble("latitude");
                                                double lng = attendee.getDouble("longitude");
                                                LatLng location = new LatLng(lat, lng);
                                                mMap.addMarker(new MarkerOptions().position(location)
                                                        .title(attendee.getString("name"))); // Use the 'name' field as the marker title
                                            }
                                        }
                                    } else {
                                        Log.w("MapActivity", "Error getting attendee documents: ", attendeeTask.getException());
                                    }
                                });
                    } else {
                        Log.w("MapActivity", "Error getting event documents: ", task.getException());
                    }
                });
    }
}
