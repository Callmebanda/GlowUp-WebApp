package com.glowup.services;



import com.glowup.model.Appointment;
import com.glowup.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    public List<Appointment> getPendingAppointmentsByOwner(int ownerId) {
        System.out.println("DEBUG: Fetching appointments for owner ID: " + ownerId); // Debug line

        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, s.name as service_name, u.name as customer_name " +
                "FROM appointments a " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN salons sl ON a.salon_id = sl.salon_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "WHERE sl.owner_id = ? AND a.status = 'pending' " +
                "ORDER BY a.appointment_date, a.start_time";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);
            System.out.println("DEBUG: Executing query: " + stmt.toString()); // Debug line

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("user_id"),
                        rs.getInt("salon_id"),
                        rs.getInt("service_id"),
                        rs.getString("appointment_date"),
                        rs.getTime("start_time"),
                        rs.getString("status"),
                        rs.getString("service_name"),
                        rs.getString("customer_name"),
                        rs.getTimestamp("created_at")
                );
                appointments.add(appt);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage()); // More detailed error
        }

        System.out.println("DEBUG: Found " + appointments.size() + " pending appointments"); // Debug line
        return appointments;
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean rescheduleAppointment(int appointmentId, String newDate, String newTime) {
        String sql = "UPDATE appointments SET appointment_date = ?, start_time = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newDate);
            stmt.setTime(2, Time.valueOf(newTime + ":00"));
            stmt.setInt(3, appointmentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error rescheduling appointment: " + e.getMessage());
            return false;
        }
    }

    // Add similar methods for getConfirmedAppointmentsByOwner()
    public List<Appointment> getConfirmedAppointmentsByOwner(int ownerId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, s.name as service_name, u.name as customer_name " +
                "FROM appointments a " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN salons sl ON a.salon_id = sl.salon_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "WHERE sl.owner_id = ? AND a.status = 'confirmed' " +
                "ORDER BY a.appointment_date, a.start_time";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("user_id"),
                        rs.getInt("salon_id"),
                        rs.getInt("service_id"),
                        rs.getString("appointment_date"),
                        rs.getTime("start_time"),
                        rs.getString("status"),
                        rs.getString("service_name"),
                        rs.getString("customer_name"),
                        rs.getTimestamp("created_at")
                );
                appointments.add(appt);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointments: " + e.getMessage());
        }
        return appointments;
    }
}