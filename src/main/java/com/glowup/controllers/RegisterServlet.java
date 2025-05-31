package com.glowup.controllers;

import com.glowup.util.DatabaseUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String userType = request.getParameter("userType");
        String salonName = request.getParameter("salonName");
        String salonAddress = request.getParameter("salonAddress");

        try (Connection conn = DatabaseUtil.getConnection()) {

            // Optional: check if email already exists
            if (emailExists(conn, email)) {
                response.sendRedirect("/register.html?error=email_exists");
                return;
            }

            boolean success;
            if ("salon_owner".equals(userType)) {
                success = registerSalonOwner(conn, name, email, password, phone, userType, salonName, salonAddress);
            } else {
                success = registerUser(conn, name, email, password, phone, userType);
            }

            if (success) {
                response.sendRedirect("/login.html?success=registered");
            } else {
                response.sendRedirect("/register.html?error=failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("/register.html?error=exception");
        }
    }

    private boolean emailExists(Connection conn, String email) throws SQLException {
        String sql = "SELECT email FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeQuery().next();
        }
    }

    private boolean registerUser(Connection conn, String name, String email,
                                 String password, String phone, String userType) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, phone, user_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            stmt.setString(5, userType);
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean registerSalonOwner(Connection conn, String name, String email,
                                       String password, String phone, String userType,
                                       String salonName, String salonAddress) throws SQLException {

        // Insert into users table
        String userSql = "INSERT INTO users (name, email, password, phone, user_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
            userStmt.setString(1, name);
            userStmt.setString(2, email);
            userStmt.setString(3, password);
            userStmt.setString(4, phone);
            userStmt.setString(5, userType);
            int affectedRows = userStmt.executeUpdate();

            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // Now insert into salons table
                    String salonSql = "INSERT INTO salons (user_id, name, address) VALUES (?, ?, ?)";
                    try (PreparedStatement salonStmt = conn.prepareStatement(salonSql)) {
                        salonStmt.setInt(1, userId);
                        salonStmt.setString(2, salonName);
                        salonStmt.setString(3, salonAddress);
                        return salonStmt.executeUpdate() > 0;
                    }
                } else {
                    return false;
                }
            }
        }
    }
}
