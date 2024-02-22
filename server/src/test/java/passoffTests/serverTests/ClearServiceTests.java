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

    @Test
    void clearTestSuccess() {  // only requires positive test case
        // add user, game, and authToken to state
        try {
            DataAccess.getInstance().createUser(new UserData("testUser", "testPass", "testEmail"));
            DataAccess.getInstance().createAuth(String.valueOf(new AuthData("testToken", "testUser")));
            DataAccess.getInstance().createGame(new GameData(1, "testUser", null, "Test Game", new ChessGame()));
        } catch (DataAccessException e) {
            fail("Failed");
        }
        ClearService clearService = new ClearService();
        Result result = clearService.clear();

        assertTrue(result.isSuccess());
        assertEquals("Database cleared successfully", result.getMessage());

        // sanity check that info was cleared
        try {
            assertTrue(DataAccess.getInstance().listUsers().isEmpty());
            assertTrue(DataAccess.getInstance().listGames().isEmpty());
            assertTrue(DataAccess.getInstance().listAuth().isEmpty());
        }  catch (DataAccessException e) {
            fail("Failed");
        }

    }

}

