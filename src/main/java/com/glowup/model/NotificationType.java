package com.glowup.model;

public enum NotificationType {
    APPOINTMENT_BOOKED("New Appointment", "You have a new appointment scheduled"),
    APPOINTMENT_CONFIRMED("Appointment Confirmed", "Your appointment has been confirmed"),
    APPOINTMENT_CANCELLED("Appointment Cancelled", "An appointment has been cancelled"),
    APPOINTMENT_REMINDER("Appointment Reminder", "You have an upcoming appointment"),
    REVIEW_REQUEST("Review Request", "Please rate your recent service"),
    SALON_UPDATE("Salon Update", "Your salon has an important update");

    private final String title;
    private final String defaultMessage;

    NotificationType(String title, String defaultMessage) {
        this.title = title;
        this.defaultMessage = defaultMessage;
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}