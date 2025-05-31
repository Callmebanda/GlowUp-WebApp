package com.glowup.controllers;

import com.glowup.model.Salon;
import com.glowup.util.DatabaseUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/salons/featured")
public class FeaturedSalonsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM salons WHERE featured = true LIMIT 3";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            List<Salon> salons = new ArrayList<>();
            while (rs.next()) {
                Salon salon = new Salon(
                        rs.getInt("salon_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("image_url"),
                        rs.getBigDecimal("rating")
                );
                salons.add(salon);
            }

            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(salons));

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}