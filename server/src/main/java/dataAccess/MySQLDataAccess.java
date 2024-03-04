package dataAccess;

import com.google.gson.Gson;
import model.UserData;
import model.GameData;
import model.AuthData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.*;

public class MySQLDataAccess implements DataAccessInterface {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private Gson serializer = new Gson();

    @Override
    public void clear() throws DataAccessException {
        // clear all data from tables
        String[] tables = new String[]{"Users", "Games", "AuthTokens"};
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String table : tables) {
                String sql = "DELETE FROM " + table;  // delete from just clears data from table, different from dropping or truncating
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to clear database");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // insert new user into Users table
        String sql = "INSERT INTO Users (username, passwordHash, email) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.username());
            statement.setString(2, encoder.encode(user.password()));  // encrypts user's password
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to create new users");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // gets user by username
        String sql = "SELECT username, passwordHash, email FROM Users WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserData(resultSet.getString("username"), resultSet.getString("passwordHash"), resultSet.getString("email"));

                } else {
                    return null;  // user doesn't exist
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to retrieve user from database");
        }
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        // puts new game into Games table
        String gameStateJson = serializer.toJson(game.game());
        String sql = "INSERT INTO Games (whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameStateJson);
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Error: unable to create game -> rows empty");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newGameID = generatedKeys.getInt(1);
                    // returns new GameData object with the new Game ID
                    return new GameData(newGameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                } else {
                    throw new SQLException("Error: unable to create game -> no ID");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to create game");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        //
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        //
        return new ArrayList<>();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        //
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        //
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        //
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        //
    }

}