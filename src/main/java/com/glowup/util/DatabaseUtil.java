package com.glowup.util;

import java.sql.*;
public class DatabaseUtil {
    public static Connection getConnection() throws SQLException {
        try {
            // Load the driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            String url = "jdbc:mysql://localhost:3306/glowupd";
            String username = "root";
            String password = "";

            Connection conn = DriverManager.getConnection(url, username, password);

            // Test the connection
            if (conn.isValid(2)) { // 2 second timeout
                System.out.println("Database connection successful");
                return conn;
            } else {
                throw new SQLException("Failed to establish valid database connection");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}