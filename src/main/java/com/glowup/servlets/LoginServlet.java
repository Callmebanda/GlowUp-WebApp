package com.glowup.servlets;

import com.glowup.util.DatabaseUtil;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password"); // Plain password
        String userType = request.getParameter("userType");

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get stored password
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, name, user_type FROM users WHERE email = ? AND user_type = ? AND password = ?");
            stmt.setString(1, email);
            stmt.setString(2, userType);
            stmt.setString(3, password); // Direct comparison with plain password

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Login successful
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("id"));
                session.setAttribute("userName", rs.getString("name"));
                session.setAttribute("userType", rs.getString("user_type"));

                // Update last login
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?")) {
                    updateStmt.setInt(1, rs.getInt("id"));
                    updateStmt.executeUpdate();
                }

                // Redirect based on user type
                response.sendRedirect(userType.equals("salon_owner")
                        ? "salon-dashboard.html"
                        : "dashboard.html");
                return;
            }

            // Login failed
            response.sendRedirect("login.html?error=Invalid+email+or+password");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=Database+error:" + e.getMessage());
        }
    }
}