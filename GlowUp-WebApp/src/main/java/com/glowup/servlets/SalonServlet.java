package com.glowup.servlets;


import com.glowup.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/salon/appointments")
public class SalonServlet extends HttpServlet {

    // Update appointment status
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DatabaseUtil.getConnection()) {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            String action = request.getParameter("action"); // "confirm" or "reject"

            String newStatus = action.equals("confirm") ? "confirmed" : "rejected";

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE appointments SET status = ? WHERE id = ?")) {

                stmt.setString(1, newStatus);
                stmt.setInt(2, appointmentId);
                stmt.executeUpdate();

                // Send notification to customer
                sendNotification(appointmentId, newStatus);

                response.sendRedirect("salon-dashboard.html?success=Appointment+" + newStatus);
            }
        } catch (SQLException e) {
            response.sendRedirect("salon-dashboard.html?error=Update+failed");
        }
    }

    private void sendNotification(int appointmentId, String newStatus) {

    }

    // Get appointments for salon
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DatabaseUtil.getConnection()) {
            int salonId = getSalonIdForOwner(request);
            String statusFilter = request.getParameter("status");
            String dateFilter = request.getParameter("date");

            String sql = "SELECT a.*, u.name as customer_name, s.name as service_name " +
                    "FROM appointments a " +
                    "JOIN users u ON a.customer_id = u.id " +
                    "JOIN services s ON a.service_id = s.id " +
                    "WHERE a.salon_id = ?";

            if (!"all".equals(statusFilter)) {
                sql += " AND a.status = ?";
            }
            if (dateFilter != null && !dateFilter.isEmpty()) {
                sql += " AND DATE(a.appointment_date) = ?";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                stmt.setInt(paramIndex++, salonId);

                if (!"all".equals(statusFilter)) {
                    stmt.setString(paramIndex++, statusFilter);
                }
                if (dateFilter != null && !dateFilter.isEmpty()) {
                    stmt.setString(paramIndex++, dateFilter);
                }

                ResultSet rs = stmt.executeQuery();
                JSONArray appointments = new JSONArray();
                while (rs.next()) {
                    JSONObject appt = new JSONObject();
                    appt.put("id", rs.getInt("id"));
                    appt.put("customer", rs.getString("customer_name"));
                    appt.put("service", rs.getString("service_name"));
                    appt.put("date", rs.getString("appointment_date"));
                    appt.put("status", rs.getString("status"));
                    appointments.put(appt);
                }

                response.setContentType("application/json");
                response.getWriter().write(appointments.toString());
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private int getSalonIdForOwner(HttpServletRequest request) {

        return 0;
    }
}