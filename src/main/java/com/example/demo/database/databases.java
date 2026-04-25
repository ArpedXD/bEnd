package com.example.demo.database;

import java.sql.*;
import java.util.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

class databasestart {
    private static HikariDataSource dataSource;

    static {
        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🔧 INITIALIZING DATABASE CONNECTION POOL");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            HikariConfig config = new HikariConfig();
            
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            System.out.println("Environment Variables Check:");
            System.out.println("  DB_URL: " + (dbUrl != null ? "✓ SET" : "✗ NOT SET"));
            System.out.println("  DB_USER: " + (dbUser != null ? "✓ SET" : "✗ NOT SET"));
            System.out.println("  DB_PASSWORD: " + (dbPassword != null ? "✓ SET" : "✗ NOT SET"));
            
            System.out.println("Connection URL: " + dbUrl);
            
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            System.out.println("Creating HikariCP DataSource...");
            dataSource = new HikariDataSource(config);
            
            System.out.println("Testing database connection...");
            try (Connection testConn = dataSource.getConnection()) {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("✅ DATABASE CONNECTION SUCCESSFUL");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
            
        } catch (Exception e) {
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.err.println("❌ FATAL: DATABASE INITIALIZATION FAILED");
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.err.println("Error Type: " + e.getClass().getName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            e.printStackTrace();
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Re-throw to prevent app from starting with broken database
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized - check startup logs");
        }
        return dataSource.getConnection();
    }
}

class games extends databasestart{
    public static void addWin(String username, String game) {
        String sql = "UPDATE user_stats us " +
                "JOIN users u ON us.user_id = u.user_id " +
                "JOIN games g ON us.game_id = g.game_id " +
                "SET us.wins = us.wins + 1 " +
                "WHERE u.username = ? AND g.game_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, game);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addLose(String username, String game) {
        String sql = "UPDATE user_stats us " +
                "JOIN users u ON us.user_id = u.user_id " +
                "JOIN games g ON us.game_id = g.game_id " +
                "SET us.losses = us.losses + 1 " +
                "WHERE u.username = ? AND g.game_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, game);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> showstats(String username,String game){
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT us.wins, us.losses " +
                "FROM user_stats us " +
                "JOIN users u ON us.user_id = u.user_id " +
                "JOIN games g ON us.game_id = g.game_id " +
                "WHERE u.username = ? AND g.game_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, game);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                stats.put("wins", rs.getInt("wins"));
                stats.put("losses", rs.getInt("losses"));
            } else {
                stats.put("wins", 0);
                stats.put("losses", 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            stats.put("wins", 0);
            stats.put("losses", 0);
        }

        return stats;
    }
}

public class databases extends games{
    public static void win(String username,String game){
        games.addWin(username, game);
    }

    public static void lose(String username, String game){
        games.addLose(username, game);
    }

    public static Map<String, Integer> stats(String username, String game){
        return games.showstats(username,game);
    }

}

