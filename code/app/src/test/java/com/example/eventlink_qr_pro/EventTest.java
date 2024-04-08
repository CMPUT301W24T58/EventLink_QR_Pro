package com.example.eventlink_qr_pro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event("Conference", "2024-05-01", "10:00", "Convention Center", "Tech Conference", true);
    }

    @Test
    void testEventConstructorAndGetters() {
        assertEquals("Conference", event.getName());
        assertEquals("2024-05-01", event.getDate());
        assertEquals("10:00", event.getTime());
        assertEquals("Convention Center", event.getLocation());
        assertEquals("Tech Conference", event.getDescription());
        assertTrue(event.isGeolocationEnabled());
    }

    @Test
    void testNameSetterAndGetter() {
        event.setName("Workshop");
        assertEquals("Workshop", event.getName());
    }

    @Test
    void testDateSetterAndGetter() {
        event.setDate("2024-06-01");
        assertEquals("2024-06-01", event.getDate());
    }

    @Test
    void testTimeSetterAndGetter() {
        event.setTime("11:00");
        assertEquals("11:00", event.getTime());
    }

    @Test
    void testLocationSetterAndGetter() {
        event.setLocation("Downtown Hall");
        assertEquals("Downtown Hall", event.getLocation());
    }

    @Test
    void testDescriptionSetterAndGetter() {
        event.setDescription("Annual Tech Workshop");
        assertEquals("Annual Tech Workshop", event.getDescription());
    }

    @Test
    void testGeolocationEnabledSetterAndGetter() {
        event.setGeolocationEnabled(false);
        assertFalse(event.isGeolocationEnabled());
    }
}
