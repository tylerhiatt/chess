package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenTests {
    private MySQLDataAccess dataAccess;
    private final UserData testUser = new UserData("testUser", "testPass", "test@test.com");

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = MySQLDataAccess.getInstance();
        dataAccess.clear();

        try {
            dataAccess.createUser(testUser);
        } catch (DataAccessException e) {
            fail("failed");
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        dataAccess.clear();
    }

    @Test
    public void testCreateAuthPositive() {
        try {
            AuthData createdAuth = dataAccess.createAuth(testUser.username());

            assertNotNull(createdAuth);
            assertEquals(testUser.username(), createdAuth.username());
            assertNotNull(createdAuth.authToken());

        } catch (DataAccessException e) {
            fail("failed");
        }

    }

    @Test
    public void testCreateAuthNegative() {
        Exception exception = assertThrows(DataAccessException.class, () -> dataAccess.createAuth("doesntExist"));

        String expectedMessage = "unable to create auth token";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetAuthPositive() {
        try {
            AuthData createdAuth = dataAccess.createAuth(testUser.username());

            AuthData getAuth = dataAccess.getAuth(createdAuth.authToken());

            assertEquals(createdAuth.authToken(), getAuth.authToken());
            assertEquals(testUser.username(), getAuth.username());

        } catch (DataAccessException e) {
            fail("failed");
        }
    }

    @Test
    public void testGetAuthNegative() {
        try {
            AuthData getAuth = dataAccess.getAuth("doesntExist");

            assertNull(getAuth);

        } catch (DataAccessException e) {
            fail("failed");
        }
    }

    @Test
    public void testDeleteAuthPositive() {
        try {
            // create, delete, then try to get token
            AuthData createdAuth = dataAccess.createAuth(testUser.username());
            dataAccess.deleteAuth(createdAuth.authToken());
            AuthData getAuth = dataAccess.getAuth(createdAuth.authToken());

            assertNull(getAuth);  // should be null
        } catch (DataAccessException e) {
            fail("failed");
        }
    }

    @Test
    public void testDeleteAuthNegative() {
        String nonExistentToken = "nonExistentToken";
        // should throw error
        Exception exception = assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth(nonExistentToken));
        String expectedMessage = "unable to delete auth token";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}