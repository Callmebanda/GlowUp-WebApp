package com.glowup.model;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private int appointmentId;
    private int userId;
    private int salonId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    // Constructors
    public Review() {}

    public Review(int appointmentId, int userId, int salonId,
                  int rating, String comment) {
        this.appointmentId = appointmentId;
        this.userId = userId;

        this.salonId = salonId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSalonId() { return salonId; }
    public void setSalonId(int salonId) { this.salonId = salonId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}