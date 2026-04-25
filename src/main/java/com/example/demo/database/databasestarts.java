package com.example.demo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class databasestarts {  // ← Changed from "class" to "public class"
    private static HikariDataSource dataSource;

    static {
        try {
            System.out.println("🔧 Initializing database connection...");
            
            HikariConfig config = new HikariConfig();
            
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            System.out.println("DB_URL from env: " + (dbUrl != null ? "SET" : "NOT SET"));
            System.out.println("DB_USER from env: " + (dbUser != null ? "SET" : "NOT SET"));
            System.out.println("DB_PASSWORD from env: " + (dbPassword != null ? "SET" : "NOT SET"));
            
            if (dbUrl == null) {
                System.out.println("⚠️ Using hardcoded database credentials");
                dbUrl = "jdbc:mysql://shinkansen.proxy.rlwy.net:26454/railway?allowPublicKeyRetrieval=true&useSSL=false&connectTimeout=30000";
                dbUser = "root";
                dbPassword = "rIwknsJBnTsIQhtOjIQfHUVaXdIMgQqE";
            }
            
            System.out.println("Connecting to: " + dbUrl);
            
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSource = new HikariDataSource(config);
            
            // Test connection immediately
            try (Connection testConn = dataSource.getConnection()) {
                System.out.println("✅ Database connection pool initialized successfully");
                System.out.println("✅ Test connection successful");
            }
            
        } catch (Exception e) {
            System.err.println("❌ FATAL: Failed to initialize database");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }
        return dataSource.getConnection();
    }
}
