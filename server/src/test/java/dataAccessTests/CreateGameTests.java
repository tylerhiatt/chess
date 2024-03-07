package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTests {
    private MySQLDataAccess dataAccess;
    private ChessGame testGame;

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = MySQLDataAccess.getInstance();
        dataAccess.clear();

        testGame = new ChessGame();
    }

    @AfterEach
    public void tearDown() throws Exception {
        dataAccess.clear();
    }

    @Test
    public void testCreateGamePositive() {
        GameData gameData = new GameData(0, "whitePlayer", "blackPlayer", "Test Game", testGame);

        try {
            GameData resultGameData = dataAccess.createGame(gameData);

            assertNotNull(resultGameData);
            assertNotEquals(0, resultGameData.gameID());  // id should not be 0 if it worked right
            assertEquals(gameData.whiteUsername(), resultGameData.whiteUsername());
            assertEquals(gameData.blackUsername(), resultGameData.blackUsername());
            assertEquals(gameData.gameName(), resultGameData.gameName());

        } catch (DataAccessException e) {
            fail("failed");
        }
    }

    @Test
    public void testCreateGameNegative() {
        GameData badGameData = new GameData(0, null, null, null, null);

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(badGameData);
        });  // should throw data access exception
    }
}