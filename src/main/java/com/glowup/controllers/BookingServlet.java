package com.glowup.controllers;

import com.glowup.model.User;
import com.glowup.util.DatabaseUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/bookings")
public class BookingServlet extends HttpServlet {

    // Get all bookings for logged in user
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        List<Map<String, String>> bookings = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Simple query to get bookings
            String sql = "SELECT a.*, s.name as service_name, sl.name as salon_name " +
                    "FROM appointments a " +
                    "JOIN services s ON a.service_id = s.service_id " +
                    "JOIN salons sl ON a.salon_id = sl.salon_id " +
                    "WHERE a.customer_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getUserId());
            ResultSet rs = stmt.executeQuery();

            // Convert to simple list of maps
            while (rs.next()) {
                Map<String, String> booking = new HashMap<>();
                booking.put("appointment_id", rs.getString("appointment_id"));
                booking.put("service_name", rs.getString("service_name"));
                booking.put("salon_name", rs.getString("salon_name"));
                booking.put("appointment_date", rs.getDate("appointment_date").toString());
                booking.put("start_time", rs.getString("start_time"));
                booking.put("end_time", rs.getString("end_time"));
                booking.put("status", rs.getString("status"));
                booking.put("notes", rs.getString("notes"));
                bookings.add(booking);
            }

            // Send as JSON
            response.setContentType("application/json");
            response.getWriter().print(new Gson().toJson(bookings));

        } catch (SQLException e) {
            response.sendError(500, "Database error");
        }
    }

    // Create new booking
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        // Read JSON data
        BufferedReader reader = request.getReader();
        Map<String, String> bookingData = new Gson().fromJson(reader, Map.class);

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Simple insert
            String sql = "INSERT INTO appointments " +
                    "(customer_id, salon_id, service_id, appointment_date, " +
                    "start_time, end_time, notes, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getUserId());
            stmt.setInt(2, Integer.parseInt(bookingData.get("salon_id")));
            stmt.setInt(3, Integer.parseInt(bookingData.get("service_id")));
            stmt.setDate(4, Date.valueOf(bookingData.get("date")));
            stmt.setTime(5, Time.valueOf(bookingData.get("start_time") + ":00"));
            stmt.setTime(6, Time.valueOf(bookingData.get("end_time") + ":00"));
            stmt.setString(7, bookingData.get("notes"));

            stmt.executeUpdate();
            response.setStatus(201); // Created

        } catch (SQLException e) {
            response.sendError(500, "Database error");
        }
    }
}