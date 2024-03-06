package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.UserData;
import model.GameData;
import model.AuthData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.*;

public class MySQLDataAccess implements DataAccessInterface {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Gson serializer = new Gson();
    private static MySQLDataAccess instance = null;

    public static MySQLDataAccess getInstance() {
        if (instance == null) {
            instance = new MySQLDataAccess();
        }
        return instance;
    }

    @Override
    public void clear() throws DataAccessException {
        // clear all data from tables
        String[] tables = new String[]{"Users", "Games", "AuthTokens"};
        try (Connection connection = DatabaseManager.getConnection()) {

            // disable foreign key checks to avoid constraint violations during clearing -> fixes issue I had when trying to register new users
            try (PreparedStatement disableFKChecks = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0;")) {
                disableFKChecks.executeUpdate();
            }

            // clear each table
            for (String table : tables) {
                String sql = "DELETE FROM " + table;  // delete from is different from dropping or truncating
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }

            // re-enable foreign key checks after clearing is done
            try (PreparedStatement enableFKChecks = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1;")) {
                enableFKChecks.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to clear database -> " + e.getMessage());
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
            statement.setString(3, user.email());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to create new users -> " + e.getMessage());
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
            throw new DataAccessException("Error: unable to retrieve user from database -> " + e.getMessage());
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
            throw new DataAccessException("Error: unable to create game -> " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // gets game from Game table
        String sql = "SELECT * FROM Games WHERE gameID = ?";
        GameData game = null;

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gameID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                String gameState = resultSet.getString("gameState");
                ChessGame chessGame = serializer.fromJson(gameState, ChessGame.class);

                game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to get game -> " + e.getMessage());
        }

        return game;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        // list games in Game table
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";

        try (Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                String gameState = resultSet.getString("gameState");
                ChessGame chessGame = serializer.fromJson(gameState, ChessGame.class);

                GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to list games -> " + e.getMessage());
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // updates games in Games table
        String sql = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ? WHERE gameID = ?";
        String gameStateJson = serializer.toJson(game.game());

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameStateJson);
            statement.setInt(5, game.gameID());

            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("Error: updating game failed -> no rows affected");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to update game -> " + e.getMessage());
        }

    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        // creates auth token for new user in AuthToken table
        String sql = "INSERT INTO AuthTokens (token, username) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, authToken);
            statement.setString(2, username);
            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new DataAccessException("Error: unable to create auth token -> no rows affected");
            }
            return new AuthData(authToken, username);
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to create auth token -> " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // gets auth token from AuthToken table
        String sql = "SELECT * FROM AuthTokens WHERE token = ?";
        AuthData authData = null;

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, authToken);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String username =resultSet.getString("username");
                authData = new AuthData(authToken, username);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to get auth token -> " + e.getMessage());
        }
        return authData;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // deletes auth token from AuthToken table
        String sql = "DELETE FROM AuthTokens WHERE token = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, authToken);
            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new DataAccessException("Error: unable to delete auth token -> no rows affected");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to delete auth token -> " + e.getMessage());
        }
    }

}