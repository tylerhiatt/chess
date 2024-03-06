package passoffTests.serverTests;

import dataAccess.MySQLDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.LoginService;
import server.RegisterService;
import server.LogoutService;
import model.UserData;
import server.Result;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTests {
    private final RegisterService registerService = new RegisterService();
    private final LoginService loginService = new LoginService();
    private final LogoutService logoutService = new LogoutService();
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
    void testLogoutSuccess() {  // positive test
         UserData userData = new UserData("logoutUser", "password", "logout@test.com");
         registerService.register(userData); // register user so i can login

        // login then logout
        Result loginResult = loginService.login(new UserData("logoutUser", "password", null));
        String authToken = loginResult.getAuthToken();

        Result logoutResult = logoutService.logout(authToken);

        assertTrue(logoutResult.isSuccess());
    }

    @Test
    void testLogoutUnauthorized() {  // negative test
        String invalidAuthToken = "invalidToken123";

        Result logoutResult = logoutService.logout(invalidAuthToken);

        assertFalse(logoutResult.isSuccess());
        assertEquals(Result.ErrorType.UNAUTHORIZED, logoutResult.getErrorType());  // should get unauthorized error from invalid token
    }


}