package com.example.eventlink_qr_pro;

public class Alert {
    private String message;
    private String date;

    public Alert(String message, String date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}

