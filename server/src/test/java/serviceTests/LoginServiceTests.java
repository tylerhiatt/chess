package serviceTests;

import dataAccess.MySQLDataAccess;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.AfterAll;
import server.LoginService;
import server.RegisterService;
import model.UserData;
import server.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTests {

    private MySQLDataAccess dataAccess;
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();

    private final UserData testUser = new UserData("testUserLogin", "testPasswordLogin", "login@test.com");
    private final UserData badUser = new UserData("badUserLogin", "correctPassword", "badUserLogin@test.com");

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
    void testLoginSuccess() { // positive test case
        registerService.register(testUser); // add user to data

        Result loginResult = loginService.login(new UserData("testUserLogin", "testPasswordLogin", null));

        assertTrue(loginResult.isSuccess());
        assertNotNull(loginResult.getAuthToken());
        assertEquals("testUserLogin", loginResult.getUsername());
    }

    @Test
    void testLoginIncorrectPassword() {  // negative test case
        registerService.register(badUser); //  add user to data

        Result loginResult = loginService.login(new UserData("badUserLogin", "wrongPassword", null));

        assertFalse(loginResult.isSuccess());  // login should fail with incorrect password
        assertEquals(Result.ErrorType.UNAUTHORIZED, loginResult.getErrorType());
    }

}
