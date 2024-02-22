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
    void testRegisterSuccess() {
        RegisterService registerService = new RegisterService();

        UserData newUser = new UserData("newUser", "aboogaboogabooga", "boii@test.com");
        Result result = registerService.register(newUser);

        assertTrue(result.isSuccess(), "Registration succeeded");
        assertNotNull(result.getAuthToken(), "Auth token is not null");

        // sanity check
        try {
            UserData registeredUser = DataAccess.getInstance().getUser("newUser");
            assertEquals(newUser.username(), registeredUser.username());
        } catch (DataAccessException e) {
            fail("Failed");
        }

    }

    @Test
    void testRegisterFail() {
        RegisterService registerService = new RegisterService();

        // put in new users
        UserData userData1 = new UserData("existingUser", "password123", "existing@test.com");
        registerService.register(userData1); // Assuming this succeeds

        UserData userData2 = new UserData("existingUser", "password456", "duplicate@test.com");
        Result result = registerService.register(userData2);

        assertFalse(result.isSuccess(), "Registration should fail for existing username");
        assertEquals(Result.ErrorType.ALREADY_TAKEN, result.getErrorType(), "Expected ALREADY_TAKEN error type");
    }
}
