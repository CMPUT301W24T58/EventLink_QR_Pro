package com.example.eventlink_qr_pro;
import java.io.Serializable;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class Attendee implements Serializable {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private double latitude;
    private double longitude;
    private String fmctoken;

    public Attendee(String id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEtoken() {
        return fmctoken;
    }

    public void setEtoken(String fmctoken) {
        this.fmctoken = fmctoken;
    }

    public void find_location(Context context){

        // Get the location service
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Check if location permission is granted
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where location permission is not granted
            return;
        }

        // Get the last known location from the network provider
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Check if the last known location is available
        if (lastKnownLocation != null) {
            this.latitude = lastKnownLocation.getLatitude();
            this.longitude = lastKnownLocation.getLongitude();

        }

    }

    void getFMCToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                setEtoken(token);
            }
        });

    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

}





