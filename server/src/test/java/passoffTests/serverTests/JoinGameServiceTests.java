package passoffTests.serverTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.UserData;
import dataAccess.DataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.ClearService;
import server.CreateGameService;
import server.JoinGameService;
import server.Result;
import chess.ChessGame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTests {
    private JoinGameService joinGameService;
    private int createdGameId;
    String validAuthToken;

    @BeforeEach
    void setUp() {  // using this implementation so that this test passes concurrently with the other tests
        joinGameService = new JoinGameService();
        CreateGameService createGameService = new CreateGameService();
        ClearService clearService = new ClearService();
        //DataAccess dataAccess = DataAccess.getInstance();
        MySQLDataAccess dataAccess = MySQLDataAccess.getInstance();

        clearService.clear();

        // register new user and create game
        UserData user = new UserData("testUserJoin", "testPassJoin", "join@test.com");
        try {
            dataAccess.createUser(user);
            AuthData authData = dataAccess.createAuth(user.username());
            validAuthToken = authData.authToken();
            Result createResult = createGameService.createGame(validAuthToken, "Test Game Join");

            createdGameId = createResult.getGameID(); // grab gameID from created game
        } catch (DataAccessException e) {
            fail("Failed");
        }
    }

    @Test
    void testJoinGameSuccess() { // positive test
        Result result = joinGameService.joinGame(validAuthToken, createdGameId, ChessGame.TeamColor.WHITE);
        assertTrue(result.isSuccess()); // join game should work with valid token
    }

    @Test
    void testJoinGameUnauthorized() {  // negative test
        String invalidAuthToken = "invalidToken";
        Result result = joinGameService.joinGame(invalidAuthToken, 1, ChessGame.TeamColor.WHITE);

        assertFalse(result.isSuccess());
        assertEquals(Result.ErrorType.UNAUTHORIZED, result.getErrorType()); // shouldn't work with invalid token
    }

    @Test
    void testBadID() {  // sanity check test for invalid gameID
        Result result = joinGameService.joinGame(validAuthToken, 0, ChessGame.TeamColor.WHITE);

        assertFalse(result.isSuccess());  // gameID 0 should mean game = null
        assertEquals(Result.ErrorType.BAD_REQUEST, result.getErrorType());

    }
}
