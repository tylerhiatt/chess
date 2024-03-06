package passoffTests.dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetGameTests {
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
    public void testGetGamePositive() throws DataAccessException {
        // put game into database
        GameData insertedGame = new GameData(0, "whitePlayer", "blackPlayer", "Test Game", testGame);
        GameData createdGame = dataAccess.createGame(insertedGame);

        // then get the game
        GameData retrievedGame = dataAccess.getGame(createdGame.gameID());

        // make sure everything matches
        assertNotNull(retrievedGame);
        assertEquals(createdGame.gameID(), retrievedGame.gameID());
        assertEquals("whitePlayer", retrievedGame.whiteUsername());
        assertEquals("blackPlayer", retrievedGame.blackUsername());
        assertEquals("Test Game", retrievedGame.gameName());

    }

    @Test
    public void testGetGameNegative() {
        final int doesntExistGameID = 9999;

        try {
            GameData retrievedGame = dataAccess.getGame(doesntExistGameID);
            assertNull(retrievedGame);
        } catch (DataAccessException e) {
            fail("failed");
        }

    }
}