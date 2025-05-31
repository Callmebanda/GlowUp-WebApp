package com.glowup.controllers;

import com.glowup.model.*;
import com.glowup.services.NotificationService;
import com.glowup.util.DatabaseUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/appointments")
public class AppointmentController extends HttpServlet {

    private NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
            return;
        }

        User user = (User) session.getAttribute("user");
        String type = request.getParameter("type");
        String appointmentId = request.getParameter("appointmentId");
        String ownerId = request.getParameter("ownerId");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = DatabaseUtil.getConnection()) {
            if (appointmentId != null) {
                getSingleAppointment(conn, Integer.parseInt(appointmentId), user, out);
            } else if ("customer".equals(user.getUserType())) {
                getCustomerAppointments(conn, user.getUserId(), type, out);
            } else if ("salon_owner".equals(user.getUserType())) {
                getSalonAppointments(conn, user.getUserId(), type, out);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        }
    }

    private void getCustomerAppointments(Connection conn, int userId, String type, PrintWriter out)
            throws SQLException {
        String sql = "SELECT a.*, s.name as service_name, sl.name as salon_name, u.name as customer_name " +
                "FROM appointments a " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN salons sl ON a.salon_id = sl.salon_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "WHERE a.user_id = ?";

        if (type != null) {
            sql += " AND " + getStatusCondition(type);
        }

        sql += " ORDER BY a.appointment_date, a.start_time";

        JsonArray jsonArray = new JsonArray();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("appointment_id", rs.getInt("appointment_id"));
                jsonObj.addProperty("service_name", rs.getString("service_name"));
                jsonObj.addProperty("salon_name", rs.getString("salon_name"));
                jsonObj.addProperty("customer_name", rs.getString("customer_name"));
                jsonObj.addProperty("appointment_date", rs.getString("appointment_date"));
                jsonObj.addProperty("start_time", rs.getString("start_time"));
                jsonObj.addProperty("status", rs.getString("status"));
                jsonArray.add(jsonObj);
            }
        }
        out.print(jsonArray.toString());
    }

    private void getSalonAppointments(Connection conn, int ownerId, String type, PrintWriter out)
            throws SQLException {
        String sql = "SELECT a.*, s.name as service_name, u.name as customer_name, sl.name as salon_name, a.notes " +
                "FROM appointments a " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN salons sl ON a.salon_id = sl.salon_id " +
                "WHERE sl.owner_id = ?";

        if (type != null) {
            sql += " AND " + getStatusCondition(type);
        }

        sql += " ORDER BY a.appointment_date, a.start_time";

        JsonArray jsonArray = new JsonArray();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("appointment_id", rs.getInt("appointment_id"));
                jsonObj.addProperty("service_name", rs.getString("service_name"));
                jsonObj.addProperty("customer_name", rs.getString("customer_name"));
                jsonObj.addProperty("salon_name", rs.getString("salon_name"));
                jsonObj.addProperty("appointment_date", rs.getString("appointment_date"));
                jsonObj.addProperty("start_time", rs.getString("start_time"));
                jsonObj.addProperty("status", rs.getString("status"));
                jsonObj.addProperty("notes", rs.getString("notes"));
                jsonArray.add(jsonObj);
            }
        }
        out.print(jsonArray.toString());
    }

    private String getStatusCondition(String type) {
        switch (type) {
            case "pending":
                return "a.status = 'pending'";
            case "upcoming":
                return "a.status = 'confirmed' AND a.appointment_date >= CURDATE()";
            case "history":
                return "(a.status = 'completed' OR a.status = 'rejected' OR a.status = 'cancelled' OR a.appointment_date < CURDATE())";
            default:
                return "1=1";
        }
    }

    private void getSingleAppointment(Connection conn, int appointmentId, User user, PrintWriter out)
            throws SQLException {
        String sql;
        JsonObject jsonObj = new JsonObject();

        if ("customer".equals(user.getUserType())) {
            sql = "SELECT a.*, s.name as service_name, sl.name as salon_name, u.name as customer_name " +
                    "FROM appointments a " +
                    "JOIN services s ON a.service_id = s.service_id " +
                    "JOIN salons sl ON a.salon_id = sl.salon_id " +
                    "JOIN users u ON a.user_id = u.user_id " +
                    "WHERE a.appointment_id = ? AND a.user_id = ?";
        } else {
            sql = "SELECT a.*, s.name as service_name, u.name as customer_name, sl.name as salon_name " +
                    "FROM appointments a " +
                    "JOIN services s ON a.service_id = s.service_id " +
                    "JOIN users u ON a.user_id = u.user_id " +
                    "JOIN salons sl ON a.salon_id = sl.salon_id " +
                    "WHERE a.appointment_id = ? AND sl.owner_id = ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            stmt.setInt(2, user.getUserId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                jsonObj.addProperty("appointment_id", rs.getInt("appointment_id"));
                jsonObj.addProperty("service_name", rs.getString("service_name"));
                jsonObj.addProperty("customer_name", rs.getString("customer_name"));
                jsonObj.addProperty("appointment_date", rs.getString("appointment_date"));
                jsonObj.addProperty("start_time", rs.getString("start_time"));
                jsonObj.addProperty("status", rs.getString("status"));
                jsonObj.addProperty("notes", rs.getString("notes"));
                jsonObj.addProperty("salon_name", rs.getString("salon_name"));
            }
        }
        out.print(jsonObj.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"salon_owner".equals(user.getUserType())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only salon owners can perform this action");
            return;
        }

        String appointmentIdStr = request.getParameter("appointmentId");
        String action = request.getParameter("action");

        if (appointmentIdStr == null || !appointmentIdStr.matches("\\d+") ||
                action == null || (!action.equals("accept") && !action.equals("reject")
                && !action.equals("complete") && !action.equals("cancel"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        int appointmentId = Integer.parseInt(appointmentIdStr);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // First get appointment details for notifications
            JsonObject appointmentDetails = getAppointmentDetails(conn, appointmentId);
            if (appointmentDetails == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Appointment not found");
                out.print(jsonResponse.toString());
                return;
            }

            String newStatus;
            String sql;

            switch (action) {
                case "accept":
                    newStatus = "confirmed";
                    sql = "UPDATE appointments a JOIN salons s ON a.salon_id = s.salon_id " +
                            "SET a.status = ? WHERE a.appointment_id = ? " +
                            "AND s.owner_id = ? AND a.status = 'pending'";
                    break;
                case "reject":
                    newStatus = "rejected";
                    sql = "UPDATE appointments a JOIN salons s ON a.salon_id = s.salon_id " +
                            "SET a.status = ? WHERE a.appointment_id = ? " +
                            "AND s.owner_id = ? AND a.status = 'pending'";
                    break;
                case "complete":
                    newStatus = "completed";
                    sql = "UPDATE appointments a JOIN salons s ON a.salon_id = s.salon_id " +
                            "SET a.status = ? WHERE a.appointment_id = ? " +
                            "AND s.owner_id = ? AND a.status = 'confirmed'";
                    break;
                case "cancel":
                    newStatus = "cancelled";
                    sql = "UPDATE appointments a JOIN salons s ON a.salon_id = s.salon_id " +
                            "SET a.status = ? WHERE a.appointment_id = ? " +
                            "AND s.owner_id = ? AND (a.status = 'pending' OR a.status = 'confirmed')";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action");
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newStatus);
                stmt.setInt(2, appointmentId);
                stmt.setInt(3, user.getUserId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Send notifications based on status change
                    sendStatusChangeNotifications(
                            appointmentDetails,
                            user.getUserId(),
                            newStatus
                    );

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Appointment status updated to " + newStatus);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "No matching appointment found or invalid status transition");
                }
                out.print(jsonResponse.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Database error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Operation failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(jsonResponse.toString());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
            return;
        }

        User user = (User) session.getAttribute("user");
        String appointmentId = request.getParameter("appointmentId");

        if (appointmentId == null || !appointmentId.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid appointment ID");
            return;
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // First get appointment details for notifications
            JsonObject appointmentDetails = getAppointmentDetails(conn, Integer.parseInt(appointmentId));
            if (appointmentDetails == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Appointment not found");
                out.print(jsonResponse.toString());
                return;
            }

            String sql;
            if ("customer".equals(user.getUserType())) {
                sql = "UPDATE appointments SET status = 'cancelled' " +
                        "WHERE appointment_id = ? AND user_id = ? AND status IN ('pending', 'confirmed')";
            } else {
                sql = "UPDATE appointments a JOIN salons s ON a.salon_id = s.salon_id " +
                        "SET a.status = 'cancelled' " +
                        "WHERE a.appointment_id = ? AND s.owner_id = ? " +
                        "AND a.status IN ('pending', 'confirmed')";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(appointmentId));
                stmt.setInt(2, user.getUserId());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // Send cancellation notifications
                    sendStatusChangeNotifications(
                            appointmentDetails,
                            user.getUserId(),
                            "cancelled"
                    );

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Appointment cancelled successfully");
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "No appointment found or not authorized");
                }
                out.print(jsonResponse.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private JsonObject getAppointmentDetails(Connection conn, int appointmentId) throws SQLException {
        String sql = "SELECT a.*, s.name as service_name, u.name as customer_name, " +
                "u.user_id as customer_id, sl.name as salon_name, sl.owner_id as owner_id " +
                "FROM appointments a " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN salons sl ON a.salon_id = sl.salon_id " +
                "WHERE a.appointment_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("appointment_id", rs.getInt("appointment_id"));
                jsonObj.addProperty("service_name", rs.getString("service_name"));
                jsonObj.addProperty("customer_name", rs.getString("customer_name"));
                jsonObj.addProperty("customer_id", rs.getInt("customer_id"));
                jsonObj.addProperty("salon_name", rs.getString("salon_name"));
                jsonObj.addProperty("owner_id", rs.getInt("owner_id"));
                jsonObj.addProperty("appointment_date", rs.getString("appointment_date"));
                jsonObj.addProperty("start_time", rs.getString("start_time"));
                jsonObj.addProperty("status", rs.getString("status"));
                return jsonObj;
            }
        }
        return null;
    }

    private void sendStatusChangeNotifications(JsonObject appointment, int actionUserId, String newStatus) {
        int customerId = appointment.get("customer_id").getAsInt();
        int salonOwnerId = appointment.get("owner_id").getAsInt();
        String serviceName = appointment.get("service_name").getAsString();
        String salonName = appointment.get("salon_name").getAsString();
        String dateTime = formatDateTime(
                appointment.get("appointment_date").getAsString(),
                appointment.get("start_time").getAsString()
        );

        // Notification for customer
        if (customerId != actionUserId) { // Don't notify if customer is performing the action
            String customerTitle = "";
            String customerMessage = "";

            switch (newStatus) {
                case "confirmed":
                    customerTitle = "Appointment Confirmed";
                    customerMessage = String.format(
                            "Your %s appointment at %s has been confirmed for %s",
                            serviceName, salonName, dateTime
                    );
                    break;
                case "rejected":
                    customerTitle = "Appointment Declined";
                    customerMessage = String.format(
                            "Your %s appointment at %s has been declined",
                            serviceName, salonName
                    );
                    break;
                case "completed":
                    customerTitle = "Service Completed";
                    customerMessage = String.format(
                            "Your %s service at %s has been marked as completed",
                            serviceName, salonName
                    );
                    break;
                case "cancelled":
                    customerTitle = "Appointment Cancelled";
                    customerMessage = String.format(
                            "Your %s appointment at %s has been cancelled",
                            serviceName, salonName
                    );
                    break;
            }

            Notification customerNotif = new Notification(
                    customerId,
                    customerTitle,
                    customerMessage,
                    "APPOINTMENT",
                    appointment.get("appointment_id").getAsInt()
            );
            notificationService.createNotification(customerNotif);
        }

        // Notification for salon owner (if not the one performing the action)
        if (salonOwnerId != actionUserId) {
            String salonTitle = "";
            String salonMessage = "";

            switch (newStatus) {
                case "cancelled":
                    salonTitle = "Appointment Cancelled";
                    salonMessage = String.format(
                            "The %s appointment with %s has been cancelled",
                            serviceName, appointment.get("customer_name").getAsString()
                    );
                    break;
                // Add other cases as needed
            }

            if (!salonTitle.isEmpty()) {
                Notification salonNotif = new Notification(
                        salonOwnerId,
                        salonTitle,
                        salonMessage,
                        "APPOINTMENT",
                        appointment.get("appointment_id").getAsInt()
                );
                notificationService.createNotification(salonNotif);
            }
        }
    }

    private String formatDateTime(String date, String time) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

            Date datePart = dateFormat.parse(date);
            Date timePart = timeFormat.parse(time);

            // Combine date and time
            Date dateTime = new Date(
                    datePart.getTime() + timePart.getTime() -
                            timeFormat.parse("00:00:00").getTime()
            );

            return outputFormat.format(dateTime);
        } catch (Exception e) {
            return date + " at " + time;
        }
    }
}