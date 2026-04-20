package com.fitTrackPro.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Configuration Class
 * Manages MySQL database connections
 */
public class DBConnection {
    
    // Database configuration constants
    private static final String URL = "jdbc:mysql://localhost:3306/fittrackpro_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    
    private static Connection connection = null;
    
    /**
     * Static initializer to load JDBC driver
     */
    static {
        try {
            Class.forName(DRIVER_CLASS);
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading MySQL JDBC Driver: " + e.getMessage());
            throw new RuntimeException("Database driver initialization failed", e);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private DBConnection() {
    }
    
    /**
     * Gets a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connection established successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to connect to database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Tests the database connection
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
