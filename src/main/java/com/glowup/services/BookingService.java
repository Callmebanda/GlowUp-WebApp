package com.glowup.services;



import com.glowup.model.Appointment;
import com.glowup.model.Salon;
import com.glowup.model.Service;
import com.glowup.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    public int createAppointment(int userId, int salonId, int serviceId, String date, String time) {
        String sql = "INSERT INTO appointments (user_id, salon_id, service_id, appointment_date, start_time, status) " +
                "VALUES (?, ?, ?, ?, ?, 'pending')";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, salonId);
            stmt.setInt(3, serviceId);
            stmt.setString(4, date);
            stmt.setTime(5, Time.valueOf(time + ":00"));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Booking error: " + e.getMessage());
            return -1;
        }
    }

    public List<Appointment> getUserAppointments(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, s.name as service_name, sl.name as salon_name " +
                "FROM appointments a " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN salons sl ON a.salon_id = sl.salon_id " +
                "WHERE a.user_id = ? ORDER BY a.appointment_date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
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
                        rs.getString("salon_name"),
                        rs.getTimestamp("created_at")
                );
                appointments.add(appt);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointments: " + e.getMessage());
        }
        return appointments;
    }

    public List<Salon> getAllSalons() {
        List<Salon> salons = new ArrayList<>();
        String sql = "SELECT salon_id, name FROM salons";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                salons.add(new Salon(
                        rs.getInt("salon_id"),
                        rs.getString("name"),
                        rs.getString("description"), rs.getString("address"), rs.getString("city"), rs.getString("phone"), rs.getString("email"), rs.getString("image_url"), rs.getBigDecimal("rating")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching salons: " + e.getMessage());
        }
        return salons;
    }

    public List<Service> getServicesBySalon(int salonId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT service_id, name, price FROM services WHERE salon_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                services.add(new Service(
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching services: " + e.getMessage());
        }
        return services;
    }
}