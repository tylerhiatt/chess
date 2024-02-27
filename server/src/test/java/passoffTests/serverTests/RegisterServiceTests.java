package passoffTests.serverTests;

import dataAccess.DataAccessException;
import server.Result;
import model.UserData;
import dataAccess.DataAccess;
import org.junit.jupiter.api.Test;
import server.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTests {

    @Test
    void testRegisterSuccess() {  // positive test
        RegisterService registerService = new RegisterService();

        UserData newUser = new UserData("testUserRegister", "testPassRegister", "register@test.com");
        Result result = registerService.register(newUser);

        assertTrue(result.isSuccess(), "Registration succeeded");
        assertNotNull(result.getAuthToken(), "Auth token is not null");

        // sanity check
        try {
            UserData registeredUser = DataAccess.getInstance().getUser("testUserRegister");
            assertEquals(newUser.username(), registeredUser.username());
        } catch (DataAccessException e) {
            fail("Failed");
        }

    }

    @Test
    void testRegisterFail() {  // negative test
        RegisterService registerService = new RegisterService();

        // put in new users
        UserData userData1 = new UserData("existingUser", "password123", "existing@test.com");
        registerService.register(userData1); // assuming this succeeds

        UserData userData2 = new UserData("existingUser", "password456", "duplicate@test.com");
        Result result = registerService.register(userData2);

        assertFalse(result.isSuccess()); // should be false for isSuccess
        assertEquals(Result.ErrorType.ALREADY_TAKEN, result.getErrorType());
    }
}
