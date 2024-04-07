package com.example.eventlink_qr_pro;

import java.io.Serializable;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Represents an attendee of an event, encapsulating details such as name, phone number, email, and location information.
 * This class also handles attendee-specific actions like locating the device and managing Firebase Cloud Messaging (FCM) tokens.
 */
public class Attendee implements Serializable {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private double latitude;
    private double longitude;
    private String fmctoken;
    private int checkInCount;
    private byte[] imageByteArray;
    private String imageUrl;
    private String deviceId;
    private boolean attendeeEnableTrackingOrNot = true;

    /**
     * Default constructor required for Firestore deserialization. Initializes a new instance of the Attendee class without setting properties.
     */
    public Attendee() {
        // Required empty constructor for Firestore deserialization
    }

    /**
     * Constructs a new instance of the Attendee class with specified id, name, phoneNumber, and email.
     *
     * @param id The unique identifier for the attendee.
     * @param name The name of the attendee.
     * @param phoneNumber The phone number of the attendee.
     * @param email The email address of the attendee.
     */
    public Attendee(String id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.imageUrl = null;
    }

    /**
     * Gets the attendee's unique identifier.
     *
     * @return The unique identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the attendee's unique identifier.
     *
     * @param id The unique identifier to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the attendee's name.
     *
     * @return The name of the attendee.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the attendee's name.
     *
     * @param name The name to set for the attendee.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the attendee's phone number.
     *
     * @return The phone number of the attendee.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the attendee's phone number.
     *
     * @param phoneNumber The phone number to set for the attendee.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the attendee's email address.
     *
     * @return The email address of the attendee.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the attendee's email address.
     *
     * @param email The email address to set for the attendee.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the Firebase Cloud Messaging (FCM) token for the attendee's device.
     *
     * @return The FCM token.
     */
    public String getEtoken() {
        return fmctoken;
    }

    /**
     * Gets the count of check-ins for the attendee.
     *
     * @return The count of check-ins.
     */
    public int getCheckInCount() {
        return checkInCount;
    }

    /**
     * Sets the count of check-ins for the attendee.
     *
     * @param checkInCount The count of check-ins to set.
     */
    public void setCheckInCount(int checkInCount) {
        this.checkInCount = checkInCount;
    }

    /**
     * Sets the Firebase Cloud Messaging (FCM) token for the attendee's device.
     *
     * @param fmctoken The FCM token to set.
     */
    public void setEtoken(String fmctoken) {
        this.fmctoken = fmctoken;
    }

    /**
     * Gets the byte array representation of the attendee's image.
     *
     * @return The image byte array.
     */
    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    /**
     * Sets the byte array representation of the attendee's image.
     *
     * @param imageByteArray The image byte array to set.
     */
    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    /**
     * Clears the byte array representation of the attendee's image.
     */
    public void clearImageByteArray() {
        this.imageByteArray = null;
    }

    /**
     * Gets the URL of the attendee's image.
     *
     * @return The image URL.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL for the attendee's image.
     *
     * @param imageUrl The image URL to set.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the device ID associated with the attendee.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID for the attendee.
     *
     * @param deviceId The device ID to set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    /**
     * Checks if tracking is enabled for the attendee.
     *
     * @return True if tracking is enabled, false otherwise.
     */
    public boolean isAttendeeEnableTrackingOrNot() {
        return attendeeEnableTrackingOrNot;
    }

    /**
     * Sets the attendee's preference for enabling tracking.
     *
     * @param attendeeEnableTrackingOrNot The tracking preference to set.
     */
    public void setAttendeeEnableTrackingOrNot(boolean attendeeEnableTrackingOrNot) {
        this.attendeeEnableTrackingOrNot = attendeeEnableTrackingOrNot;
    }

    /**
     * Determines the current location of the device and sets the latitude and longitude fields of the attendee.
     * This method requires the ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission to be granted.
     *
     * @param context The context from which the system Location Service can be accessed.
     */
    public void find_location(Context context) {

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

    /**
     * Retrieves the current Firebase Cloud Messaging (FCM) token for the device and assigns it to the fmctoken field of the attendee.
     */
    void getFMCToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                setEtoken(token);
            }
        });

    }

    /**
     * Gets the latitude of the attendee's location.
     *
     * @return The latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude of the attendee's location.
     *
     * @return The longitude.
     */
    public double getLongitude() {
        return longitude;
    }
}
