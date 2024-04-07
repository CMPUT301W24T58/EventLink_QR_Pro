package com.example.eventlink_qr_pro;

/**
 * Represents an alert message within the application. An alert consists of a message
 * and a date indicating when the alert was issued. This class provides a simple model
 * for storing and retrieving alert information.
 */
public class Alert {
    private String message;
    private String date;

    /**
     * Constructs a new Alert with the specified message and date.
     *
     * @param message The message of the alert. This message contains the information
     *                or notification that the alert intends to convey.
     * @param date    The date the alert was issued. The format of the date is not specified
     *                by this class and should be consistent with how the application intends
     *                to use it.
     */
    public Alert(String message, String date) {
        this.message = message;
        this.date = date;
    }

    /**
     * Returns the message of this alert.
     *
     * @return The message of the alert.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the date when this alert was issued.
     *
     * @return The date of the alert.
     */
    public String getDate() {
        return date;
    }
}

