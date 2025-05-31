package com.glowup.model;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int userId;
    private int salonId;
    private int serviceId;
    private LocalDateTime bookingTime;
    private String status; // "pending", "confirmed", "cancelled"

    // Constructors, getters, setters
    public Booking() {}

    public Booking(int userId, int salonId, int serviceId, LocalDateTime bookingTime) {
        this.userId = userId;
        this.salonId = salonId;
        this.serviceId = serviceId;
        this.bookingTime = bookingTime;
        this.status = "pending";
    }

    // Add all getters and setters here
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    // ... (other getters/setters)
}