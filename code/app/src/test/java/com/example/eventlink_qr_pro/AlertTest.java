package com.example.eventlink_qr_pro;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * The {@code AlertTest} class contains unit tests for the {@code Alert} class,
 * focusing on testing the construction and behavior of {@code Alert} objects. These tests
 * ensure that {@code Alert} objects are correctly created with specified message and date,
 * including edge cases with empty strings.
 * <p>
 * The tests in this class include:
 * <ul>
 *     <li>Testing the constructor and getter methods with normal input.</li>
 *     <li>Testing the constructor and getter methods with empty strings as input.</li>
 * </ul>
 * </p>
 */
class AlertTest {
    /**
     * Tests the {@code Alert} constructor and its getter methods with expected, non-empty
     * input values for message and date. Verifies that the message and date of the created
     * {@code Alert} object match the provided input values.
     */
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
    /**
     * Tests the {@code Alert} constructor and its getter methods with empty strings as input
     * for both the message and the date. Verifies that the message and date of the created
     * {@code Alert} object are both empty strings, thus handling edge cases gracefully.
     */
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
