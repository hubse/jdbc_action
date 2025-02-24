package com.jdbc.crud;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
    private LoggerUtil() {
        // Private constructor to prevent instantiation
    }

    public static void configureLogger(Logger logger) {
        // Remove default handlers
        logger.setUseParentHandlers(false);
        
        // Create console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        
        // Set formatter
        consoleHandler.setFormatter(new CustomLogFormatter());
        
        // Add handler to logger
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
    }
}
