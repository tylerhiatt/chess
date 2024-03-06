package passoffTests.dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class ClearTests {
    private MySQLDataAccess dataAccess;
    private final UserData testUser = new UserData("testClear", "testPassClear", "clear@test.com");
    private final GameData testGame = new GameData(1, "clearWhite", "clearBlack", "clearGame", new ChessGame());

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = MySQLDataAccess.getInstance();

        // set up some user, game, and authToken data
        dataAccess.createUser(testUser);
        dataAccess.createGame(testGame);
        dataAccess.createAuth(testUser.username());
    }

    @AfterEach
    public void tearDown() throws Exception {
        dataAccess.clear();
    }

    @Test
    void testClearPositive() {
        try (Connection connection = DatabaseManager.getConnection()){
            dataAccess.clear();

            assertTrue(isTableEmpty(connection, "Users"));
            assertTrue(isTableEmpty(connection, "Games"));
            assertTrue(isTableEmpty(connection, "AuthTokens"));

        } catch (SQLException | DataAccessException e ) {
            fail("failed");
        }

    }

    // method to check if table is empty to verify that clear works as I expect
    private boolean isTableEmpty(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM " + tableName;
        try (PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("count") == 0;  // true if count is 0
            }
        }
        return false;
    }


}