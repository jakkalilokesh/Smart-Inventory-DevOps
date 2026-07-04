package com.smartinventory.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database utility class for managing database connections using connection pooling.
 * Uses Apache Commons DBCP2 for efficient connection pooling.
 * Reads database configuration from db.properties file.
 */
public class DatabaseUtil {
    private static final Logger logger = LogManager.getLogger(DatabaseUtil.class);
    private static BasicDataSource dataSource;

    static {
        try {
            initializeDataSource();
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Initializes the data source with configuration from properties file,
     * overriding with system environment variables if available.
     */
    private static void initializeDataSource() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
            }
        }

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));

        // Check for environment variables (production / container support)
        String envHost = System.getenv("DB_HOST");
        String envPort = System.getenv("DB_PORT");
        String envName = System.getenv("DB_NAME");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        String dbUrl;
        if (envHost != null && envName != null) {
            String port = envPort != null ? envPort : "3306";
            dbUrl = "jdbc:mysql://" + envHost + ":" + port + "/" + envName + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            logger.info("Using database configuration from environment: host={}, port={}, db={}", envHost, port, envName);
        } else {
            dbUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/smartinventory?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            logger.info("Using database configuration from properties file");
        }

        String dbUser = envUser != null ? envUser : props.getProperty("db.username", "root");
        String dbPass = envPass != null ? envPass : props.getProperty("db.password", "");

        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);

        dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.pool.initialSize", "5")));
        dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.pool.maxTotal", "20")));
        dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.pool.maxIdle", "10")));
        dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "5")));
        dataSource.setMaxWaitMillis(Long.parseLong(props.getProperty("db.pool.maxWaitMillis", "10000")));
        logger.info("Database connection pool initialized successfully");
    }

    /**
     * Gets a database connection from the connection pool.
     * 
     * @return Connection object
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            logger.debug("Database connection obtained from pool");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to get database connection", e);
            throw e;
        }
    }

    /**
     * Closes the connection properly.
     * 
     * @param connection the connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection returned to pool");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Closes the connection pool and cleans up resources.
     * Should be called during application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null) {
            try {
                dataSource.close();
                logger.info("Database connection pool shut down successfully");
            } catch (SQLException e) {
                logger.error("Error shutting down database connection pool", e);
            }
        }
    }

    /**
     * Tests the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && connection.isValid(5);
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
}
