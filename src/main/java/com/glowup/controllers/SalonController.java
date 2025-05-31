package com.glowup.controllers;

import com.glowup.model.Service;
import com.glowup.model.User;
import com.glowup.util.DatabaseUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/salon/*")
public class SalonController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getUserType().equals("salon_owner")) {
            response.sendRedirect(request.getContextPath() + "/auth/login.html");
            return;
        }

        if (path.equals("/services")) {
            // Get services for a salon
            int salonId = Integer.parseInt(request.getParameter("salonId"));

            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "SELECT * FROM services WHERE salon_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, salonId);

                ResultSet rs = stmt.executeQuery();
                request.setAttribute("services", rs);
                request.getRequestDispatcher("/dashboard/salon/services.jsp").forward(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/dashboard/salon/?error=1");
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getUserType().equals("salon_owner")) {
            response.sendRedirect(request.getContextPath() + "/auth/login.html");
            return;
        }

        String path = request.getPathInfo();

        if (path.equals("/add-service")) {
            // Add new service
            int salonId = Integer.parseInt(request.getParameter("salonId"));
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int duration = Integer.parseInt(request.getParameter("duration"));
            String category = request.getParameter("category");
            String gender = request.getParameter("gender");

            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "INSERT INTO services (salon_id, name, description, price, duration_minutes, category, gender) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, salonId);
                stmt.setString(2, name);
                stmt.setString(3, description);
                stmt.setDouble(4, price);
                stmt.setInt(5, duration);
                stmt.setString(6, category);
                stmt.setString(7, gender);

                int rowsAffected = stmt.executeUpdate();
                response.sendRedirect(request.getContextPath() + "/dashboard/salon/services.jsp?added=" + (rowsAffected > 0 ? "1" : "0"));
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/dashboard/salon/services.jsp?error=1");
            }
        }
    }
}