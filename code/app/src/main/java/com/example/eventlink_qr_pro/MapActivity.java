package com.example.eventlink_qr_pro;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
    private boolean geolocationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        eventName = getIntent().getStringExtra("eventName");
        Button btnDisableTracking = findViewById(R.id.btn_disable_tracking);
        Button btnEnableTracking = findViewById(R.id.btn_enable_tracking);
        Button back = findViewById(R.id.btn_back);

        if (eventName != null && !eventName.isEmpty()) {
            db.collection("events").document(eventName)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("geolocationEnabled")) {
                            geolocationEnabled = documentSnapshot.getBoolean("geolocationEnabled");
                            if (geolocationEnabled) {
                                // Only setup the map fragment if geolocation is enabled
                                setupMapFragment();
                            } else {
                                Log.d("MapActivity", "Geolocation is disabled for this event.");
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("MapActivity", "Error fetching event document", e));
        } else {
            Log.e("MapActivity", "Event name is null or empty.");
        }

        btnDisableTracking.setOnClickListener(v -> {
            if (eventName != null && !eventName.isEmpty()) {
                disableGeolocation();
            } else {
                Log.e("MapActivity", "Event name is null or empty.");
            }
        });
        btnEnableTracking.setOnClickListener(v -> {
            if (eventName != null && !eventName.isEmpty()) {
                enableGeolocationAndShowAttendees();
            } else {
                Log.e("MapActivity", "Event name is null or empty.");
            }
        });
        back.setOnClickListener(view -> {
            finish();
        });
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (geolocationEnabled) {
            fetchAttendeesAndMarkThem(eventName);
        }
    }

    private void fetchAttendeesAndMarkThem(String eventName) {
        // First, check if geolocation is enabled for the event.
        db.collection("events").document(eventName)
                .get()
                .addOnSuccessListener(eventDocument -> {
                    if (eventDocument.exists()) {
                        Boolean eventGeolocationEnabled = eventDocument.getBoolean("geolocationEnabled");
                        if (Boolean.TRUE.equals(eventGeolocationEnabled)) {
                            // If geolocation is enabled for the event, proceed to fetch and show attendees based on their tracking preference.
                            db.collection("events").document(eventName).collection("attendees")
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Boolean attendeeTrackingEnabled = document.getBoolean("attendeeEnableTrackingOrNot");
                                                if (Boolean.TRUE.equals(attendeeTrackingEnabled)) {
                                                    // Only add marker if both event geolocation and attendee tracking are enabled.
                                                    if (document.contains("latitude") && document.contains("longitude")) {
                                                        double lat = document.getDouble("latitude");
                                                        double lng = document.getDouble("longitude");
                                                        LatLng location = new LatLng(lat, lng);
                                                        mMap.addMarker(new MarkerOptions().position(location).title(document.getString("name")));
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.w("MapActivity", "Error getting attendee documents: ", task.getException());
                                        }
                                    });
                        } else {
                            // Clear the map if geolocation is disabled for the event
                            if (mMap != null) {
                                mMap.clear();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("MapActivity", "Error fetching event document", e));
    }


    private void disableGeolocation() {
        // Update Firestore to set geolocationEnabled to false for the current event
        db.collection("events").document(eventName)
                .update("geolocationEnabled", false)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MapActivity", "Geolocation tracking has been disabled for this event.");
                    // Clear any markers from the map
                    if (mMap != null) {
                        mMap.clear();
                    }
                })
                .addOnFailureListener(e -> Log.e("MapActivity", "Error disabling geolocation tracking", e));
    }

    private void enableGeolocationAndShowAttendees() {
        // Update Firestore to set geolocationEnabled to true for the current event
        db.collection("events").document(eventName)
                .update("geolocationEnabled", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MapActivity", "Geolocation tracking has been enabled for this event.");
                    setupMapFragment();
                    fetchAttendeesAndMarkThem(eventName);
                })
                .addOnFailureListener(e -> Log.e("MapActivity", "Error enabling geolocation tracking", e));
    }
}
