package passoffTests.serverTests;

import server.Result;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTests {

    @Test
    void testRegisterSuccess() {
        RegisterService registerService = new RegisterService();

        UserData userData = new UserData("newUser", "aboogaboogabooga", "boii@test.com");
        Result result = registerService.register(userData);

        assertTrue(result.isSuccess(), "Registration succeeded");
        assertNotNull(result.getAuthToken(), "Auth token is not null");
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
