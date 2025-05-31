package com.glowup.util;

<<<<<<< HEAD
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
=======
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/glowupd?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // your username
    private static final String PASSWORD = ""; // your password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to database...");
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connection established!");
        return conn;
    }
>>>>>>> 1fc0e8b4aead21815ee64711e16f9a850303b202
}