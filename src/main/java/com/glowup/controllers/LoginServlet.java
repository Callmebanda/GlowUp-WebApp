package com.glowup.controllers;

import com.glowup.model.User;
import com.glowup.util.DatabaseUtil;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Use prepared statement with password hashing
            String sql = "SELECT user_id, name, email, user_type FROM users WHERE email = ? AND password = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Create User object
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setUserType(rs.getString("user_type"));

                        // Generate session token
                        String sessionToken = UUID.randomUUID().toString();

                        // Store in session
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        session.setAttribute("sessionToken", sessionToken);
                        session.setMaxInactiveInterval(30 * 60); // 30 minutes

                        // Update last login in database
                        updateLastLogin(conn, user.getUserId(), sessionToken);

                        // Redirect based on user type
                        String redirectPath = request.getContextPath() +
                                ("salon_owner".equals(user.getUserType()) ?
                                        "/salon-dashboard.html" : "/customer-dashboard.html");

                        response.sendRedirect(redirectPath);
                    } else {
                        response.sendRedirect(request.getContextPath() +
                                "/login.html?error=invalid");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/login.html?error=database");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/login.html?error=exception");
        }
    }

    private void updateLastLogin(Connection conn, int userId, String sessionToken)
            throws SQLException {
        String sql = "UPDATE users SET last_login = NOW(), session_token = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionToken);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired");
            return;
        }

        response.setContentType("application/json");
        response.getWriter().print(new Gson().toJson(user));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getSession().invalidate();
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
