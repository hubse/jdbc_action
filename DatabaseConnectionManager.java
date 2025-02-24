package com.jdbc.crud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnectionManager {
    private static final Logger logger = Logger.getLogger(DatabaseConnectionManager.class.getName());
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");

    private DatabaseConnectionManager() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            if (DB_URL == null || USER == null || PASS == null) {
                throw new IllegalStateException("Database configuration missing. Please set DB_URL, DB_USER, and DB_PASS environment variables");
            }
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.log(Level.INFO, "Database connection established");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error establishing database connection", e);
        } catch (IllegalStateException e) {
            logger.log(Level.SEVERE, "Configuration error: " + e.getMessage());
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                logger.log(Level.INFO, "Database connection closed");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing database connection", e);
            }
        }
    }
}
