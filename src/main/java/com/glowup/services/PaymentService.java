package com.glowup.services;



import com.glowup.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentService {
    public boolean processPayment(int appointmentId, int userId, BigDecimal amount, String paymentMethod) {
        String sql = "INSERT INTO payments (appointment_id, amount, payment_method, status) " +
                "VALUES (?, ?, ?, 'completed')";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            stmt.setBigDecimal(2, amount);
            stmt.setString(3, paymentMethod);

            boolean success = stmt.executeUpdate() > 0;

            if (success) {
                // Award 1 point per $10 spent
                int points = amount.divide(BigDecimal.TEN).intValue();
                new LoyaltyService().addPoints(userId, points, "booking");

            }

            return success;

        } catch (SQLException e) {
            System.err.println("Payment processing error: " + e.getMessage());
            return false;
        }
    }

    public boolean recordDeposit(int appointmentId, int userId, BigDecimal amount) {
        return processPayment(appointmentId, userId, amount, "deposit");
    }
}