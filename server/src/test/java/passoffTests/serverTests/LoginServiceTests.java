package passoffTests.serverTests;

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

    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();

    @Test
    void testLoginSuccess() { // positive test case
        UserData userData = new UserData("testUserLogin", "testPasswordLogin", "login@test.com");
        registerService.register(userData); // add user to data

        Result loginResult = loginService.login(new UserData("testUserLogin", "testPasswordLogin", null));

        assertTrue(loginResult.isSuccess());
        assertNotNull(loginResult.getAuthToken());
        assertEquals("testUserLogin", loginResult.getUsername());
    }

    @Test
    void testLoginIncorrectPassword() {  // negative test case
        UserData userData = new UserData("testUser", "correctPassword", "test@test.com");
        registerService.register(userData); //  add user to data

        Result loginResult = loginService.login(new UserData("testUser", "wrongPassword", null));

        assertFalse(loginResult.isSuccess());  // login should fail with incorrect password
        assertEquals(Result.ErrorType.UNAUTHORIZED, loginResult.getErrorType());
    }

}
