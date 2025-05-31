package com.glowup.services;


import com.glowup.model.Review;
import com.glowup.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewService {
    public boolean submitReview(Review review) {
        String sql = "INSERT INTO reviews (appointment_id, user_id, salon_id, rating, comment) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getAppointmentId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getSalonId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error submitting review: " + e.getMessage());
            return false;
        }
    }
}