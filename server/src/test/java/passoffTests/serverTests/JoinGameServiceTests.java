package passoffTests.serverTests;

import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataAccess.DataAccess;
import server.JoinGameService;
import server.Result;
import chess.ChessGame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTests {
    private final JoinGameService joinGameService = new JoinGameService();
    private final DataAccess dataAccess = DataAccess.getInstance();
    String validAuthToken;

    @Test
    void testJoinGameSuccess() {
        // register authToken for user and create game
        try {
            UserData user = new UserData("testUser", "testPass", "test@test.com");
            dataAccess.createUser(user);
            AuthData authData = dataAccess.createAuth(user.username());
            dataAccess.createGame(new GameData(1, null, null, "Test Game", new ChessGame()));

            validAuthToken = authData.authToken();

        } catch (DataAccessException e) {
            fail("Failed");
        }

        Result result = joinGameService.joinGame(validAuthToken, 1, ChessGame.TeamColor.WHITE);
        assertTrue(result.isSuccess()); // join game should work with valid token
    }

    @Test
    void testJoinGameUnauthorized() {
        String invalidAuthToken = "invalidToken";
        Result result = joinGameService.joinGame(invalidAuthToken, 1, ChessGame.TeamColor.WHITE);

        assertFalse(result.isSuccess());
        assertEquals(Result.ErrorType.UNAUTHORIZED, result.getErrorType()); // shouldn't work with invalid token
    }
}
