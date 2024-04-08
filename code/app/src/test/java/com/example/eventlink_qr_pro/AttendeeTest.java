package com.example.eventlink_qr_pro;

import static org.junit.jupiter.api.Assertions.*;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.CompletableFuture;
/**
 * The {@code AttendeeTest} class conducts unit tests on the {@code Attendee} class,
 * ensuring that all attendee attributes are correctly managed. These tests cover the setting and
 * getting of various attendee properties, such as IDs, names, phone numbers, emails, check-in counts,
 * e-tokens, image data, device IDs, and location data.
 *
 * Furthermore, it tests the functionality for enabling and disabling tracking for an attendee,
 * as well as accurately finding and setting an attendee's location when permission is granted.
 *
 */
public class AttendeeTest{
    private Attendee attendee;
    private LocationManager locationManager;
    private Context context;
    /**
     * Sets up mock objects for {@code Context} and {@code LocationManager} before each test.
     * This method also initializes the {@code Attendee} instance that will be used in the tests.
     */
    @BeforeEach
    public void setUp() {
        attendee = new Attendee();
        context = mock(Context.class);
        locationManager = mock(LocationManager.class);
        when(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager);
    }
    /**
     * Tests whether the ID of an attendee can be set and retrieved correctly.
     */
    @Test
    public void testGetId() {
        attendee.setId("123");
        assertEquals("123", attendee.getId());
    }
    /**
     * Verifies that setting an attendee's ID stores the correct value.
     */
    @Test
    public void testSetId() {
        String expectedId = "uniqueId123";
        attendee.setId(expectedId);
        String actualId = attendee.getId();
        assertEquals(expectedId, actualId, "The retrieved ID should match the set value.");
    }

    /**
     * Tests the retrieval of an attendee's name after it has been set.
     */
    @Test
    public void testGetName() {
        attendee.setName("John Doe");
        assertEquals("John Doe", attendee.getName());
    }
    /**
     * Checks if the attendee's name is correctly stored when set.
     */
    @Test
    public void testSetName() {
        String name = "Jane Doe";
        attendee.setName(name);
        assertEquals(name, attendee.getName(), "The name should match the set value.");
    }

    /**
     * Tests the functionality to retrieve an attendee's phone number after it has been set.
     */
    @Test
    public void testGetPhoneNumber() {
        attendee.setPhoneNumber("1234567890");
        assertEquals("1234567890", attendee.getPhoneNumber());
    }
    /**
     * Verifies that setting an attendee's phone number correctly updates the value.
     */
    @Test
    public void testSetPhoneNumber() {
        String phoneNumber = "0987654321";
        attendee.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, attendee.getPhoneNumber(), "The phone number should match the set value.");
    }

    /**
     * Tests the ability to get an attendee's email after it has been set.
     */
    @Test
    public void testGetEmail() {
        attendee.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", attendee.getEmail());
    }
    /**
     * Ensures that an attendee's email can be set and then retrieved accurately.
     */
    @Test
    public void testSetEmail() {
        String email = "new.email@example.com";
        attendee.setEmail(email);
        assertEquals(email, attendee.getEmail(), "The email should match the set value.");
    }

    /**
     * Checks if the attendee's check-in count is accurately retrieved after being set.
     */
    @Test
    public void testGetCheckInCount() {
        attendee.setCheckInCount(5);
        assertEquals(5, attendee.getCheckInCount());
    }
    /**
     * Verifies the functionality to set and then correctly retrieve the attendee's check-in count.
     */
    @Test
    public void testSetCheckInCount() {
        int checkInCount = 10;
        attendee.setCheckInCount(checkInCount);
        assertEquals(checkInCount, attendee.getCheckInCount(), "The check-in count should match the set value.");
    }

    /**
     * Tests the ability to get an attendee's e-token after setting it.
     */
    @Test
    public void testGetEtoken() {
        attendee.setEtoken("abc123");
        assertEquals("abc123", attendee.getEtoken());
    }
    /**
     * Ensures that an attendee's e-token can be set and retrieved accurately.
     */
    @Test
    public void testSetEtoken() {
        String etoken = "newToken123";
        attendee.setEtoken(etoken);
        assertEquals(etoken, attendee.getEtoken(), "The EToken should match the set value.");
    }

    /**
     * Verifies that the attendee's image byte array can be set and retrieved correctly.
     */
    @Test
    public void testGetImageByteArray() {
        byte[] byteArray = {1, 2, 3};
        attendee.setImageByteArray(byteArray);
        assertArrayEquals(byteArray, attendee.getImageByteArray());
    }
    /**
     * Checks if setting the image byte array of an attendee correctly updates its value.
     */
    @Test
    public void testSetImageByteArray() {
        byte[] byteArray = {4, 5, 6};
        attendee.setImageByteArray(byteArray);
        assertArrayEquals(byteArray, attendee.getImageByteArray(), "The image byte array should match the set value.");
    }

    /**
     * Tests the functionality to clear an attendee's image byte array.
     */
    @Test
    public void testClearImageByteArray() {
        byte[] byteArray = {1, 2, 3};
        attendee.setImageByteArray(byteArray);
        attendee.clearImageByteArray();
        assertNull(attendee.getImageByteArray());
    }
    /**
     * Verifies that an attendee's image URL can be retrieved after setting it.
     */
    @Test
    public void testGetImageUrl() {
        attendee.setImageUrl("https://example.com/image.jpg");
        assertEquals("https://example.com/image.jpg", attendee.getImageUrl());
    }
    /**
     * Ensures that setting an attendee's image URL updates it accurately.
     */
    @Test
    public void testSetImageUrl() {
        String imageUrl = "https://example.com/new_image.jpg";
        attendee.setImageUrl(imageUrl);
        assertEquals(imageUrl, attendee.getImageUrl(), "The image URL should match the set value.");
    }

    /**
     * Tests the functionality to get an attendee's device ID after it has been set.
     */
    @Test
    public void testGetDeviceId() {
        attendee.setDeviceId("device123");
        assertEquals("device123", attendee.getDeviceId());
    }
    /**
     * Verifies that setting an attendee's device ID accurately updates the stored value.
     */
    @Test
    public void testSetDeviceId() {
        String deviceId = "device456";
        attendee.setDeviceId(deviceId);
        assertEquals(deviceId, attendee.getDeviceId(), "The device ID should match the set value.");
    }
    /**
     * Tests the attendee's ability to enable or disable tracking and verifies the state.
     */
    @Test
    public void testIsAttendeeEnableTrackingOrNot() {
        attendee.setAttendeeEnableTrackingOrNot(true);
        assertTrue(attendee.isAttendeeEnableTrackingOrNot());
    }
    /**
     * Checks the functionality to set and verify the attendee's tracking status.
     */
    @Test
    public void testSetAttendeeEnableTrackingOrNot() {
        attendee.setAttendeeEnableTrackingOrNot(true);
        assertTrue(attendee.isAttendeeEnableTrackingOrNot(), "Attendee tracking should be enabled.");

        attendee.setAttendeeEnableTrackingOrNot(false);
        assertFalse(attendee.isAttendeeEnableTrackingOrNot(), "Attendee tracking should be disabled.");
    }

    /**
     * Tests the functionality of finding and setting an attendee's location.
     * It verifies if the location is accurately found when permission is granted.
     */
    @Test
    public void testFindLocation() {
        // Mock location
        Location location = mock(Location.class);
        when(location.getLatitude()).thenReturn(10.0);
        when(location.getLongitude()).thenReturn(20.0);

        // Mock permission
        when(context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)).thenReturn(PackageManager.PERMISSION_GRANTED);
        when(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).thenReturn(location);

        // Call the method
        attendee.find_location(context);

        // Check if latitude and longitude are set
        assertEquals(10.0, attendee.getLatitude());
        assertEquals(20.0, attendee.getLongitude());
    }

    /**
     * Verifies that the attendee's latitude is correctly set and retrieved after finding the location.
     */
    @Test
    public void testGetLatitude() {
        // Arrange
        double expectedLatitude = 10.0;
        Location mockedLocation = mock(Location.class);
        when(mockedLocation.getLatitude()).thenReturn(expectedLatitude);
        when(context.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_GRANTED);
        when(locationManager.getLastKnownLocation(anyString())).thenReturn(mockedLocation);

        // Act
        attendee.find_location(context); // This method internally sets latitude based on the mocked location
        double actualLatitude = attendee.getLatitude();

        // Assert
        assertEquals(expectedLatitude, actualLatitude, "The retrieved latitude should match the expected value.");
    }


    /**
     * Ensures that the attendee's longitude is accurately set and retrieved upon location detection.
     */
    @Test
    public void testGetLongitude() {
        // Arrange
        double expectedLongitude = 20.0;
        Location mockedLocation = mock(Location.class);
        when(mockedLocation.getLongitude()).thenReturn(expectedLongitude);
        when(context.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_GRANTED);
        when(locationManager.getLastKnownLocation(anyString())).thenReturn(mockedLocation);

        // Act
        attendee.find_location(context); // This method internally sets longitude based on the mocked location
        double actualLongitude = attendee.getLongitude();

        // Assert
        assertEquals(expectedLongitude, actualLongitude, "The retrieved longitude should match the expected value.");
    }

}