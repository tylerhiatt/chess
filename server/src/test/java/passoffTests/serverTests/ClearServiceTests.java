package passoffTests.serverTests;

import dataAccess.DataAccessException;
import model.UserData;
import model.GameData;
import model.AuthData;
import server.ClearService;
import server.Result;
import dataAccess.DataAccess;
import chess.ChessGame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ClearServiceTests {
    private final DataAccess dataAccess = DataAccess.getInstance();

    @Test
    void clearTestSuccess() {  // only requires positive test case
        // add user, game, and authToken to state
        try {
            dataAccess.createUser(new UserData("testUserClear", "testPassClear", "clearEmail"));
            dataAccess.createAuth(String.valueOf(new AuthData("testToken", "testUserClear")));
            dataAccess.createGame(new GameData(1, "testUserClear", null, "Test Game", new ChessGame()));

        } catch (DataAccessException e) {
            fail("Failed");
        }
        ClearService clearService = new ClearService();
        Result result = clearService.clear();

        assertTrue(result.isSuccess());
        assertEquals("Database cleared successfully", result.getMessage());

    }

}


