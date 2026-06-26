package com.sams.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class that provides a single shared JDBC connection to the MySQL database.
 * DATA ACCESS LAYER — shared by all DAO classes.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/sams_db?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "1234";

    private static Connection connection = null;

    private DBConnection() {}   // prevent instantiation

    /**
     * Returns a single reused connection (creates it on first call).
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Closes the connection (call on app shutdown).
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
