package com.example.eventlink_qr_pro;

/**
 * Represents an event with details such as name, date, time, location, and description.
 * It includes the option to enable geolocation for the event.
 */
public class Event {
    private String name;
    private String date;
    private String time;
    private String location;
    private String description;
    private boolean geolocationEnabled;

    /**
     * Default constructor for creating an instance without setting any initial values.
     */
    public Event() {}
    /**
     * Constructs a new Event with specified details.
     *
     * @param name The name of the event.
     * @param date The date of the event.
     * @param time The time of the event.
     * @param location The location of the event.
     * @param description A description of the event.
     * @param geolocationEnabled Indicates whether geolocation is enabled for the event.
     */
    public Event(String name, String date, String time, String location, String description, boolean geolocationEnabled) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.geolocationEnabled = geolocationEnabled;
    }

    // Getters and setters
    /**
     * Gets the name of the event.
     *
     * @return The name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event.
     *
     * @param name The name to set for the event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the date of the event.
     *
     * @return The date of the event.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the event.
     *
     * @param date The date to set for the event.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the time of the event.
     *
     * @return The time of the event.
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time of the event.
     *
     * @param time The time to set for the event.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets the location of the event.
     *
     * @return The location of the event.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the event.
     *
     * @param location The location to set for the event.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the description of the event.
     *
     * @return The description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     *
     * @param description The description to set for the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Checks if geolocation is enabled for the event.
     *
     * @return True if geolocation is enabled, false otherwise.
     */
    public boolean isGeolocationEnabled() {
        return geolocationEnabled;
    }

    /**
     * Enables or disables geolocation for the event.
     *
     * @param geolocationEnabled Set to true to enable geolocation, false to disable it.
     */
    public void setGeolocationEnabled(boolean geolocationEnabled) {
        this.geolocationEnabled = geolocationEnabled;
    }
}

