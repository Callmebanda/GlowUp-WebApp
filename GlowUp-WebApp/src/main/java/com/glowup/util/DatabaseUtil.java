package com.glowup.util;

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
}