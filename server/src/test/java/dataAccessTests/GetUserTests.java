package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetUserTests {
    private MySQLDataAccess dataAccess;
    private final UserData testUser = new UserData("testUserGet", "testPassGet", "get@test.com");

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = MySQLDataAccess.getInstance();
        dataAccess.clear();

        // create a user first
        dataAccess.createUser(testUser);
    }

    @AfterEach
    public void tearDown() throws Exception {
        dataAccess.clear();
    }

    @Test
    public void getUserPositive() {
        try {
            // try to get user that was just created
            UserData retrievedUser = dataAccess.getUser(testUser.username());

            assertNotNull(retrievedUser);
            assertEquals(testUser.username(), retrievedUser.username());
            assertEquals(testUser.email(), retrievedUser.email());

        } catch (DataAccessException e) {
            fail("Failed");
        }
    }

    @Test
    public void getUserNegative() {
        try {
            UserData retrievedUser = dataAccess.getUser("doesntExist");
            assertNull(retrievedUser); // shouldn't exist
        } catch (DataAccessException e) {
            fail("Failed");
        }
    }




}