package passoffTests.dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListGameTests {
    private MySQLDataAccess dataAccess;
    private ChessGame testGame1;
    private ChessGame testGame2;

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = MySQLDataAccess.getInstance();
        dataAccess.clear();

        testGame1 = new ChessGame();
        testGame2 = new ChessGame();
    }

    @AfterEach
    public void tearDown() throws Exception {
        dataAccess.clear();
    }

    @Test
    public void testListGamesPositive() {
        GameData game1 = new GameData(0, "1", "2", "First Game", testGame1);
        GameData game2 = new GameData(0, "3", "4", "Second Game", testGame2);

        try {
            dataAccess.createGame(game1);
            dataAccess.createGame(game2);

            List<GameData> retrievedGames = dataAccess.listGames();

            assertEquals(2, retrievedGames.size());
            assertTrue(retrievedGames.stream().anyMatch(game -> game.gameName().equals("First Game")));
            assertTrue(retrievedGames.stream().anyMatch(game -> game.gameName().equals("Second Game")));

        } catch (DataAccessException e) {
            fail("failed");
        }

    }

    @Test
    public void testListGamesNegative() {
        try {
            List<GameData> retrievedGames = dataAccess.listGames();
            assertTrue(retrievedGames.isEmpty());

        } catch (DataAccessException e) {
            fail("failed");
        }
    }

}