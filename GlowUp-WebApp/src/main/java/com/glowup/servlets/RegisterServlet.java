package com.glowup.servlets;

import com.glowup.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password"); // Plain password
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String userType = request.getParameter("userType");
        String salonName = request.getParameter("salonName");
        String salonAddress = request.getParameter("salonAddress");

        // Basic validation
        if (password == null || password.length() < 8) {
            response.sendRedirect("register.html?error=Password+must+be+at+least+8+characters");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check if email exists
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT id FROM users WHERE email = ?")) {
                checkStmt.setString(1, email);

                if (checkStmt.executeQuery().next()) {
                    response.sendRedirect("register.html?error=Email+already+exists");
                    return;
                }
            }

            // Insert new user with plain password
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO users (email, password, name, phone, user_type) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, email);
                insertStmt.setString(2, password); // Storing plain password
                insertStmt.setString(3, name);
                insertStmt.setString(4, phone);
                insertStmt.setString(5, userType);

                int affectedRows = insertStmt.executeUpdate();

                // If salon owner, add salon information
                if (affectedRows > 0 && "salon_owner".equals(userType)) {
                    try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int userId = rs.getInt(1);

                            try (PreparedStatement salonStmt = conn.prepareStatement(
                                    "INSERT INTO salon_providers (user_id, salon_name, salon_address) VALUES (?, ?, ?)")) {
                                salonStmt.setInt(1, userId);
                                salonStmt.setString(2, salonName);
                                salonStmt.setString(3, salonAddress);
                                salonStmt.executeUpdate();
                            }
                        }
                    }
                }
            }

            response.sendRedirect("login.html?success=Registration+successful");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.html?error=Database+error:" + e.getMessage());
        }
    }
}