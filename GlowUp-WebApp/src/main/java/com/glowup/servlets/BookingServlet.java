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

@WebServlet("/bookings")
public class BookingServlet extends HttpServlet {

    // Create new booking
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get booking details from request
            int customerId = (int) request.getSession().getAttribute("userId");
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            String appointmentTime = request.getParameter("appointmentTime");

            // Calculate end time based on service duration
            String endTime = calculateEndTime(serviceId, appointmentTime);

            // Insert booking
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO appointments (customer_id, service_id, staff_id, salon_id, " +
                            "appointment_date, end_time, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')")) {

                stmt.setInt(1, customerId);
                stmt.setInt(2, serviceId);
                stmt.setInt(3, staffId);
                stmt.setInt(4, getSalonIdForService(serviceId));
                stmt.setString(5, appointmentTime);
                stmt.setString(6, endTime);

                stmt.executeUpdate();
                response.sendRedirect("confirmation.html");
            }
        } catch (SQLException e) {
            response.sendRedirect("booking.html?error=Booking+failed");
        }
    }

    private int getSalonIdForService(int serviceId) {
        return 0;
    }

    private String calculateEndTime(int serviceId, String appointmentTime) {

        return appointmentTime;
    }

    // Get available time slots
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DatabaseUtil.getConnection()) {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            String date = request.getParameter("date");

            // Get existing appointments for the staff member on this date
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT appointment_date, end_time FROM appointments " +
                            "WHERE staff_id = ? AND DATE(appointment_date) = ? " +
                            "AND status IN ('pending', 'confirmed')")) {

                stmt.setInt(1, staffId);
                stmt.setString(2, date);

                ResultSet rs = stmt.executeQuery();
                JSONArray bookedSlots = new JSONArray();
                while (rs.next()) {
                    JSONObject slot = new JSONObject();
                    slot.put("start", rs.getString("appointment_date"));
                    slot.put("end", rs.getString("end_time"));
                    bookedSlots.put(slot);
                }

                response.setContentType("application/json");
                response.getWriter().write(bookedSlots.toString());
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}