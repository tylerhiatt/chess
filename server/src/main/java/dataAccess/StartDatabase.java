package dataAccess;

import java.sql.*;

public class StartDatabase {

    // sql strings for creating each table
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS Users (" +
                                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                                "username VARCHAR(255) NOT NULL UNIQUE," +
                                                "passwordHash VARCHAR(255) NOT NULL," +
                                                "email VARCHAR(255)," +
                                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS Games (" +
                                                "gameID INT AUTO_INCREMENT PRIMARY KEY," +
                                                "whiteUsername VARCHAR(255)," +
                                                "blackUsername VARCHAR(255)," +
                                                "gameName VARCHAR(255) NOT NULL," +
                                                "gameState TEXT," +
                                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_AUTH_TABLE = "CREATE TABLE IF NOT EXISTS AuthTokens (" +
                                                    "token VARCHAR(255) PRIMARY KEY," +
                                                    "username VARCHAR(255) NOT NULL," +
                                                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                                    "FOREIGN KEY (username) REFERENCES Users(username));";


    // methods to create the tables
    public static void start() {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.createStatement()) {
                // creates tables
                statement.executeUpdate(CREATE_USERS_TABLE);
                statement.executeUpdate(CREATE_GAMES_TABLE);
                statement.executeUpdate(CREATE_AUTH_TABLE);
            }
        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }

}