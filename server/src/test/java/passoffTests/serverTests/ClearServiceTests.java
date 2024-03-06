package passoffTests.serverTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.UserData;
import model.GameData;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.ClearService;
import server.Result;
import dataAccess.DataAccess;
import chess.ChessGame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ClearServiceTests {
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
    void clearTestSuccess() {  // only requires positive test case
        ClearService clearService = new ClearService();
        Result result = clearService.clear();

        assertTrue(result.isSuccess());
        assertEquals("Database cleared successfully", result.getMessage());

    }

}


