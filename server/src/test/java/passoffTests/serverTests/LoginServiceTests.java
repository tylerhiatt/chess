package passoffTests.serverTests;

import server.LoginService;
import server.RegisterService;
import model.UserData;
import server.Result;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTests {

    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();

    @Test
    void testLoginSuccess() { // positive test case
        UserData userData = new UserData("testUser", "testPassword", "testEmail@example.com");
        registerService.register(userData); // add user to data

        Result loginResult = loginService.login(new UserData("testUser", "testPassword", null));

        assertTrue(loginResult.isSuccess());
        assertNotNull(loginResult.getAuthToken());
        assertEquals("testUser", loginResult.getUsername());
    }

    @Test
    void testLoginIncorrectPassword() {  // negative test case
        UserData userData = new UserData("testUser", "correctPassword", "testEmail@example.com");
        registerService.register(userData); //  add user to data

        Result loginResult = loginService.login(new UserData("testUser", "wrongPassword", null));

        assertFalse(loginResult.isSuccess());  // login should fail with incorrect password
        assertEquals(Result.ErrorType.UNAUTHORIZED, loginResult.getErrorType());
    }

}
