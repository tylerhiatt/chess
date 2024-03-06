package passoffTests.dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateGameTests {
    private MySQLDataAccess dataAccess;

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = MySQLDataAccess.getInstance();
        dataAccess.clear();

    }

    @AfterEach
    public void tearDown() throws Exception {
        dataAccess.clear();
    }

    @Test
    public void testUpdateGamePositive() {
        GameData ogGame = new GameData(0, "OGWhite", "OGBlack", "OGGame", new ChessGame());

        try {
            GameData createdGame = dataAccess.createGame(ogGame);

            GameData updatedGame = new GameData(createdGame.gameID(), "updatedWhite", "updatedBlack", "Updated Game", new ChessGame());
            dataAccess.updateGame(updatedGame);

            GameData retrievedGame = dataAccess.getGame(updatedGame.gameID());
            assertEquals(updatedGame.whiteUsername(), retrievedGame.whiteUsername());
            assertEquals(updatedGame.blackUsername(), retrievedGame.blackUsername());
            assertEquals(updatedGame.gameName(), retrievedGame.gameName());

        } catch (DataAccessException e) {
            fail("failed");
        }
    }

    @Test
    public void testUpdateGameNegative() {
        GameData badGame = new GameData(9999, "badWhite", "badBlack", "Bad Game", new ChessGame());

        Exception exception = assertThrows(DataAccessException.class, () -> dataAccess.updateGame(badGame));
        String expectedMessage = "Error: updating game failed -> no rows affected";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


}