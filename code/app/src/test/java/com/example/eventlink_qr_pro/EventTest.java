package com.example.eventlink_qr_pro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the {@code Event} class, ensuring proper functionality of its constructor,
 * getters, and setters. Tests validate the correct handling of event properties such as name,
 * date, time, location, description, and geolocation status. This includes verification of:
 * <ul>
 *     <li>Proper object initialization and property retrieval.</li>
 *     <li>Ability to update and retrieve each property.</li>
 * </ul>
 */

class EventTest {
    private Event event;
    /**
     * Initializes an {@code Event} object before each test with a set of predefined values
     * to ensure a consistent testing environment. The event is set up with default properties
     * that will be used in subsequent tests to verify getter and setter functionality.
     */
    @BeforeEach
    void setUp() {
        event = new Event("Conference", "2024-05-01", "10:00", "Convention Center", "Tech Conference", true);
    }
    /**
     * Tests the {@code Event} constructor and getter methods to ensure that an event object
     * is correctly instantiated with the given properties, and that these properties can
     * be retrieved accurately.
     */
    @Test
    void testEventConstructorAndGetters() {
        assertEquals("Conference", event.getName());
        assertEquals("2024-05-01", event.getDate());
        assertEquals("10:00", event.getTime());
        assertEquals("Convention Center", event.getLocation());
        assertEquals("Tech Conference", event.getDescription());
        assertTrue(event.isGeolocationEnabled());
    }
    /**
     * Verifies that the event name can be updated and retrieved correctly, ensuring
     * the {@code setName} and {@code getName} methods function as expected.
     */
    @Test
    void testNameSetterAndGetter() {
        event.setName("Workshop");
        assertEquals("Workshop", event.getName());
    }
    /**
     * Tests the functionality of updating and retrieving the event's date, confirming
     * the accuracy of the {@code setDate} and {@code getDate} methods.
     */
    @Test
    void testDateSetterAndGetter() {
        event.setDate("2024-06-01");
        assertEquals("2024-06-01", event.getDate());
    }
    /**
     * Ensures that the event's time can be set and then retrieved accurately, validating
     * the proper operation of the {@code setTime} and {@code getTime} methods.
     */
    @Test
    void testTimeSetterAndGetter() {
        event.setTime("11:00");
        assertEquals("11:00", event.getTime());
    }
    /**
     * Verifies the ability to update and retrieve the event's location, testing
     * the {@code setLocation} and {@code getLocation} methods for correct functionality.
     */
    @Test
    void testLocationSetterAndGetter() {
        event.setLocation("Downtown Hall");
        assertEquals("Downtown Hall", event.getLocation());
    }
    /**
     * Confirms that the event's description can be updated and accurately retrieved,
     * ensuring the {@code setDescription} and {@code getDescription} methods work as intended.
     */
    @Test
    void testDescriptionSetterAndGetter() {
        event.setDescription("Annual Tech Workshop");
        assertEquals("Annual Tech Workshop", event.getDescription());
    }
    /**
     * Tests the ability to update and check the event's geolocation status, verifying
     * the {@code setGeolocationEnabled} and {@code isGeolocationEnabled} methods' functionality.
     */
    @Test
    void testGeolocationEnabledSetterAndGetter() {
        event.setGeolocationEnabled(false);
        assertFalse(event.isGeolocationEnabled());
    }
}
