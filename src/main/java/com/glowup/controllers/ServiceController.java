package com.glowup.controllers;

import com.glowup.model.Service;
import com.glowup.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceController {

    // Add a new service
    public boolean addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (salon_id, name, description, price, duration_minutes, category, gender) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, service.getSalonId());
            stmt.setString(2, service.getName());
            stmt.setString(3, service.getDescription());
            stmt.setDouble(4, service.getPrice());
            stmt.setInt(5, service.getDurationMinutes());
            stmt.setString(6, service.getCategory());
            stmt.setString(7, service.getGender());

            return stmt.executeUpdate() > 0;
        }
    }

    // Get all services for a salon
    public List<Service> getServicesBySalon(int salonId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE salon_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("service_id"));
                service.setSalonId(rs.getInt("salon_id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getDouble("price"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                service.setCategory(rs.getString("category"));
                service.setGender(rs.getString("gender"));
                services.add(service);
            }
        }
        return services;
    }

    // Remove a service
    public boolean removeService(int serviceId) throws SQLException {
        String sql = "DELETE FROM services WHERE service_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serviceId);
            return stmt.executeUpdate() > 0;
        }
    }
}