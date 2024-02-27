package passoffTests.serverTests;

import dataAccess.DataAccessException;
import model.GameData;
import model.UserData;
import model.AuthData;
import dataAccess.DataAccess;
import server.Result;
import server.ListGameService;
import server.ClearService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ListGameServiceTests {
    private final ListGameService listGameService = new ListGameService();
    private final DataAccess dataAccess = DataAccess.getInstance();
    private final ClearService clearService = new ClearService();
    private String validAuthToken;

    @Test
    public void testListGamesSuccess() {  // positive test case
        clearService.clear();  // make sure starting with empty slate

        GameData game1 = new GameData(1, "white1", "black1", "Game 1", null);
        GameData game2 = new GameData(2, "white2", "black2", "Game 2", null);

        // register authToken for user
        try {
            UserData user = new UserData("testUserList", "testPassList", "list@test.com");
            dataAccess.createUser(user);
            AuthData authData = dataAccess.createAuth(user.username());
            validAuthToken = authData.authToken();
        } catch (DataAccessException e) {
            fail("Failed");
        }

        // create games
        try {
            dataAccess.createGame(game1);
            dataAccess.createGame(game2);
        } catch (DataAccessException e) {
            fail("Failed");
        }

        Result result = listGameService.listGame(validAuthToken);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getGames().size()); // make sure both games are listed there
    }

    @Test
    public void testListGamesUnauthorized() {  // negative test case
        Result result = listGameService.listGame("invalid_auth_token");

        assertFalse(result.isSuccess());
        assertEquals("Error: unauthorized", result.getMessage()); // authToken shouldn't exist
    }

}