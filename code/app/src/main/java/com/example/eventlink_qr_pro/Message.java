package com.example.eventlink_qr_pro;

/**
 * Represents a message with a title and description. This class can be used for notifications,
 * alerts, or any scenario where a simple message structure is needed.
 */
public class Message {
    private String title;
    private String description;

    /**
     * Default constructor for creating an empty message.
     */
    public Message() {

    }

    /**
     * Constructs a new Message with the specified title and description.
     *
     * @param title The title of the message.
     * @param description The description or body of the message.
     */
    public Message(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Gets the title of the message.
     *
     * @return The title of the message.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description or body of the message.
     *
     * @return The description of the message.
     */
    public String getDescription() {
        return description;
    }

    
}

