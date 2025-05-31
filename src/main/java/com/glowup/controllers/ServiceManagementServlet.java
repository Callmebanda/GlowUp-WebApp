package com.glowup.controllers;

import com.glowup.model.Service;
import com.glowup.model.User;
import com.glowup.util.DatabaseUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/manage-services")
public class ServiceManagementServlet extends HttpServlet {

    // Get all services for the current salon
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getUserType().equals("salon_owner")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM services WHERE salon_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getUserId());

            ResultSet rs = stmt.executeQuery();
            List<Service> services = new ArrayList<>();

            while (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("service_id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setCategory(rs.getString("category"));
                service.setDurationMinutes(rs.getInt("duration_minutes"));
                service.setPrice(rs.getDouble("price"));
                service.setGender(rs.getString("gender"));
                services.add(service);
            }

            // Return as JSON
            response.setContentType("application/json");
            response.getWriter().print(new Gson().toJson(services));

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    // Handle add/edit/delete actions
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getUserType().equals("salon_owner")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = request.getParameter("action");
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            switch (action) {
                case "add":
                    addService(conn, request, user.getUserId());
                    result.put("success", true);
                    break;
                case "edit":
                    updateService(conn, request);
                    result.put("success", true);
                    break;
                case "delete":
                    deleteService(conn, request);
                    result.put("success", true);
                    break;
                default:
                    result.put("success", false);
                    result.put("message", "Invalid action");
            }

            response.setContentType("application/json");
            response.getWriter().print(new Gson().toJson(result));

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new Gson().toJson(result));
        }
    }

    private void addService(Connection conn, HttpServletRequest request, int salonId) throws SQLException {
        String sql = "INSERT INTO services (salon_id, name, description, category, duration_minutes, price, gender) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, salonId);
        stmt.setString(2, request.getParameter("name"));
        stmt.setString(3, request.getParameter("description"));
        stmt.setString(4, request.getParameter("category"));
        stmt.setInt(5, Integer.parseInt(request.getParameter("duration")));
        stmt.setDouble(6, Double.parseDouble(request.getParameter("price")));
        stmt.setString(7, request.getParameter("gender"));

        stmt.executeUpdate();
    }

    private void updateService(Connection conn, HttpServletRequest request) throws SQLException {
        String sql = "UPDATE services SET name = ?, description = ?, category = ?, " +
                "duration_minutes = ?, price = ?, gender = ? WHERE service_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, request.getParameter("name"));
        stmt.setString(2, request.getParameter("description"));
        stmt.setString(3, request.getParameter("category"));
        stmt.setInt(4, Integer.parseInt(request.getParameter("duration")));
        stmt.setDouble(5, Double.parseDouble(request.getParameter("price")));
        stmt.setString(6, request.getParameter("gender"));
        stmt.setInt(7, Integer.parseInt(request.getParameter("service_id")));

        stmt.executeUpdate();
    }

    private void deleteService(Connection conn, HttpServletRequest request) throws SQLException {
        String sql = "DELETE FROM services WHERE service_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, Integer.parseInt(request.getParameter("service_id")));

        stmt.executeUpdate();
    }
}
