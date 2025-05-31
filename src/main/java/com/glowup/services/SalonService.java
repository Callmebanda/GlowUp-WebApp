package com.glowup.services;


import com.glowup.model.Salon;
import com.glowup.model.Service;
import com.glowup.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalonService {
    public List<Salon> getAllSalons() {
        List<Salon> salons = new ArrayList<>();
        String sql = "SELECT salon_id, name, address FROM salons";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Salon salon = new Salon(
                );
                salons.add(salon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salons;
    }
    public List<Service> getServicesBySalon(int salonId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT service_id, name, price, duration_minutes, category, gender " +
                "FROM services WHERE salon_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                services.add(new Service(
                        // Add salonId parameter
                        // Add null for description
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching services: " + e.getMessage());
        }
        return services;
    }

    public List<Salon> getSalonsByOwner(int ownerId) {
        List<Salon> salons = new ArrayList<>();
        String sql = "SELECT salon_id, name FROM salons WHERE owner_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();

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

    public boolean addService(Service service) {
        String sql = "INSERT INTO services (salon_id, name, description, category, duration_minutes, price, gender) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, service.getSalonId());
            stmt.setString(2, service.getName());
            stmt.setString(3, service.getDescription());
            stmt.setString(4, service.getCategory());
            stmt.setInt(5, service.getDurationMinutes());
            stmt.setDouble(6, service.getPrice());
            stmt.setString(7, service.getGender());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding service: " + e.getMessage());
            return false;
        }
    }

    public boolean updateService(Service service) {
        String sql = "UPDATE services SET name = ?, description = ?, category = ?, " +
                "duration_minutes = ?, price = ?, gender = ? WHERE service_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setString(3, service.getCategory());
            stmt.setInt(4, service.getDurationMinutes());
            stmt.setDouble(5, service.getPrice());
            stmt.setString(6, service.getGender());
            stmt.setInt(7, service.getServiceId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating service: " + e.getMessage());
            return false;
        }
    }

    public boolean removeService(int serviceId) {
        String sql = "DELETE FROM services WHERE service_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serviceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error removing service: " + e.getMessage());
            return false;
        }
    }

    public Service getServiceById(int serviceId) {
        String sql = "SELECT * FROM services WHERE service_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serviceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Service(
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching service: " + e.getMessage());
        }
        return null;
    }
}