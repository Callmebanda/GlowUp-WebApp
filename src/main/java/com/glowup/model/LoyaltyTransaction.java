package com.glowup.model;

import java.sql.Timestamp;

public class LoyaltyTransaction {
    private Timestamp date;
    private int points;
    private String reason;

    // Constructor, getters, and toString()
    public LoyaltyTransaction(Timestamp date, int points, String reason) {
        this.date = date;
        this.points = points;
        this.reason = reason;
    }

    // Getters...
    public Timestamp getDate() { return date; }
    public int getPoints() { return points; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return date + " | " + (points > 0 ? "+" : "") + points + " | " + reason;
    }
}