package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataAccess.DataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.Result;
import server.CreateGameService;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTests {

    private final CreateGameService createGameService = new CreateGameService();
    private MySQLDataAccess dataAccess;
    private String validAuthToken;

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = MySQLDataAccess.getInstance();
        dataAccess.clear();

        // register authToken for user
        try {
            UserData user = new UserData("testUserCreate", "testPassCreate", "create@test.com");
            dataAccess.createUser(user);
            AuthData authData = dataAccess.createAuth(user.username());
            validAuthToken = authData.authToken();
        } catch (DataAccessException e) {
            fail("Failed");
        }

    }

    @AfterEach
    public void tearDown() throws Exception {
        dataAccess.clear();
    }

    @Test
    void testCreateGameSuccess() {  // positive test case
        Result result = createGameService.createGame(validAuthToken, "Test Game Create");

        assertTrue(result.isSuccess());
        assertNotEquals(0, result.getGameID()); // make sure game ID isn't 0

        // sanity check that game is listed in dataAccess
        try {
            GameData createdGame = dataAccess.getGame(result.getGameID());
            assertEquals("Test Game Create", createdGame.gameName());
        } catch (DataAccessException e) {
            fail("Failed");
        }
    }

    @Test
    void testCreateGameUnauthorized() {  // negative test case
        Result result = createGameService.createGame("invalidAuthToken", "Bad Game");

        assertFalse(result.isSuccess());
        assertEquals(Result.ErrorType.UNAUTHORIZED, result.getErrorType()); // auth Token shouldn't be there
    }
}