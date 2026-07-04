package com.smartinventory.listener;

import com.smartinventory.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application lifecycle listener.
 * Initializes resources on application startup and cleans up on shutdown.
 */
@WebListener
public class ApplicationListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(ApplicationListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("SmartInventory application starting...");
        
        try {
            // Test database connection
            boolean connected = DatabaseUtil.testConnection();
            if (connected) {
                logger.info("Database connection test successful");
            } else {
                logger.warn("Database connection test failed. Application may not function properly.");
            }
            
            logger.info("SmartInventory application started successfully");
        } catch (Exception e) {
            logger.error("Error during application initialization", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("SmartInventory application shutting down...");
        
        try {
            // Close database connection pool
            DatabaseUtil.shutdown();
            logger.info("Database connection pool closed");
        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
        
        logger.info("SmartInventory application shut down successfully");
    }
}
