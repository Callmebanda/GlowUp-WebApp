package com.glowup.servlets;

import com.glowup.Gson;
import com.glowup.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebServlet("/services")
public class ServicesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        String maxPrice = request.getParameter("maxPrice");

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM services WHERE is_active = TRUE";
            List<Object> params = new ArrayList<>();

            if (category != null && !category.isEmpty()) {
                sql += " AND category = ?";
                params.add(category);
            }

            if (maxPrice != null && !maxPrice.isEmpty()) {
                sql += " AND price <= ?";
                params.add(Double.parseDouble(maxPrice));
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> services = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> service = new HashMap<>();
                service.put("id", rs.getInt("id"));
                service.put("name", rs.getString("name"));
                service.put("description", rs.getString("description"));
                service.put("price", rs.getDouble("price"));
                service.put("duration_minutes", rs.getInt("duration_minutes"));
                service.put("salon_id", rs.getInt("salon_id"));
                services.add(service);
            }

            response.setContentType("application/json");
            response.getWriter().write(new Gson().toString());

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}