package com.glowup.controllers;

import com.glowup.model.*;
import com.glowup.util.DatabaseUtil;
import com.google.gson.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/booking")
public class BookingController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            // Get salon ID from request
            String salonIdParam = request.getParameter("salonId");
            if (salonIdParam == null || !salonIdParam.matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("error", "Valid salonId parameter is required");
                out.print(jsonResponse.toString());
                return;
            }
            int salonId = Integer.parseInt(salonIdParam);

            try (Connection conn = DatabaseUtil.getConnection()) {
                // Get salon details
                Salon salon = getSalonById(conn, salonId);
                if (salon == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    jsonResponse.addProperty("error", "Salon not found");
                    out.print(jsonResponse.toString());
                    return;
                }

                // Get services for this salon
                List<Service> services = getServicesBySalon(conn, salonId);

                // Build JSON response
                JsonObject salonJson = new JsonObject();
                salonJson.addProperty("salon_id", salon.getSalonId());
                salonJson.addProperty("name", salon.getName());
                salonJson.addProperty("description", salon.getDescription());
                salonJson.addProperty("address", salon.getAddress());
                salonJson.addProperty("city", salon.getCity());
                salonJson.addProperty("phone", salon.getPhone());
                salonJson.addProperty("image_url", salon.getImageUrl());
                salonJson.addProperty("rating", salon.getRating());

                JsonArray servicesArray = new JsonArray();
                for (Service service : services) {
                    JsonObject serviceJson = new JsonObject();
                    serviceJson.addProperty("service_id", service.getServiceId());
                    serviceJson.addProperty("name", service.getName());
                    serviceJson.addProperty("description", service.getDescription());
                    serviceJson.addProperty("price", service.getPrice());
                    serviceJson.addProperty("duration_minutes", service.getDurationMinutes());
                    serviceJson.addProperty("category", service.getCategory());
                    servicesArray.add(serviceJson);
                }

                jsonResponse.add("salon", salonJson);
                jsonResponse.add("services", servicesArray);
                out.print(jsonResponse.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Internal server error");
            out.print(jsonResponse.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            // Check authentication
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", "Please login first");
                out.print(jsonResponse.toString());
                return;
            }

            // Read JSON request
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            JsonObject jsonRequest = JsonParser.parseString(sb.toString()).getAsJsonObject();

            // Get parameters
            int salonId = jsonRequest.get("salonId").getAsInt();
            int serviceId = jsonRequest.get("serviceId").getAsInt();
            String date = jsonRequest.get("date").getAsString();
            String time = jsonRequest.get("time").getAsString();
            String notes = jsonRequest.has("notes") ? jsonRequest.get("notes").getAsString() : null;

            // Get user from session
            User user = (User) session.getAttribute("user");

            // Database operations
            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.setAutoCommit(false);

                // Check availability
                if (!isTimeSlotAvailable(conn, salonId, date, time)) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    jsonResponse.addProperty("error", "Time slot already booked");
                    out.print(jsonResponse.toString());
                    return;
                }

                // Create booking
                int appointmentId = createBooking(conn, user.getUserId(), salonId, serviceId, date, time, notes);
                conn.commit();

                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("appointment_id", appointmentId);
                jsonResponse.addProperty("message", "Appointment booked successfully");
                out.print(jsonResponse.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Error: " + e.getMessage());
            out.print(jsonResponse.toString());
        }
    }

    // Helper methods
    private Salon getSalonById(Connection conn, int salonId) throws SQLException {
        String sql = "SELECT * FROM salons WHERE salon_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salonId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Salon salon = new Salon();
                salon.setSalonId(rs.getInt("salon_id"));
                salon.setName(rs.getString("name"));
                salon.setDescription(rs.getString("description"));
                salon.setAddress(rs.getString("address"));
                salon.setCity(rs.getString("city"));
                salon.setPhone(rs.getString("phone"));
                salon.setImageUrl(rs.getString("image_url"));
                salon.setRating(rs.getDouble("rating"));
                return salon;
            }
            return null;
        }
    }

    private List<Service> getServicesBySalon(Connection conn, int salonId) throws SQLException {
        String sql = "SELECT * FROM services WHERE salon_id = ?";
        List<Service> services = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salonId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("service_id"));
                service.setSalonId(rs.getInt("salon_id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setCategory(rs.getString("category"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                service.setPrice(rs.getDouble("price"));
                services.add(service);
            }
        }
        return services;
    }

    private boolean isTimeSlotAvailable(Connection conn, int salonId, String date, String time) throws SQLException {
        String sql = "SELECT 1 FROM appointments WHERE salon_id = ? AND appointment_date = ? AND start_time = ? " +
                "AND status IN ('pending', 'confirmed')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salonId);
            stmt.setString(2, date);
            stmt.setString(3, time + ":00");
            return !stmt.executeQuery().next();
        }
    }

    private int createBooking(Connection conn, int userId, int salonId, int serviceId,
                              String date, String time, String notes) throws SQLException {
        String sql = "INSERT INTO appointments (user_id, salon_id, service_id, appointment_date, " +
                "start_time, notes, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, salonId);
            stmt.setInt(3, serviceId);
            stmt.setString(4, date);
            stmt.setString(5, time + ":00");
            stmt.setString(6, notes);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("Failed to get generated appointment ID");
        }
    }
}