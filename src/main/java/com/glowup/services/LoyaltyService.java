package com.glowup.services;


import com.glowup.model.LoyaltyTransaction;
import com.glowup.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoyaltyService {
    public int getUserPoints(int userId) {
        String sql = "SELECT SUM(points) as total FROM loyalty_points WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("total") : 0;

        } catch (SQLException e) {
            System.err.println("Error getting points: " + e.getMessage());
            return 0;
        }
    }

    public List<LoyaltyTransaction> getPointHistory(int userId) {
        List<LoyaltyTransaction> history = new ArrayList<>();
        String sql = "SELECT * FROM loyalty_points WHERE user_id = ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(new LoyaltyTransaction(
                        rs.getTimestamp("transaction_date"),
                        rs.getInt("points"),
                        rs.getString("reason")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting point history: " + e.getMessage());
        }
        return history;
    }
    public boolean addPoints(int userId, int points, String reason) {
        String sql = "INSERT INTO loyalty_points (user_id, points, reason) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, points);
            stmt.setString(3, reason);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding loyalty points: " + e.getMessage());
            return false;
        }
    }

    public boolean redeemPoints(int userId, int points) {
        if (getUserPoints(userId) < points) return false;

        String sql = "INSERT INTO loyalty_points (user_id, points, reason) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, -points); // Deduct points
            stmt.setString(3, "redeemed_for_discount");
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error redeeming points: " + e.getMessage());
            return false;
        }
    }
}