package com.example.eventlink_qr_pro;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertTest {

    @Test
    void testAlertConstructorAndGetters() {
        // Setup
        String expectedMessage = "Test Alert Message";
        String expectedDate = "2024-04-08";

        // Execute
        Alert alert = new Alert(expectedMessage, expectedDate);

        // Verify
        assertEquals(expectedMessage, alert.getMessage(), "The message should match the input.");
        assertEquals(expectedDate, alert.getDate(), "The date should match the input.");
    }

    @Test
    void testAlertWithEmptyMessageAndDate() {
        // Setup
        String expectedMessage = "";
        String expectedDate = "";

        // Execute
        Alert alert = new Alert(expectedMessage, expectedDate);

        // Verify
        assertEquals(expectedMessage, alert.getMessage(), "The message should be empty.");
        assertEquals(expectedDate, alert.getDate(), "The date should be empty.");
    }
}
