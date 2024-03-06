package passoffTests.dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTests {
    private MySQLDataAccess dataAccess;
    private final UserData testUser = new UserData("testCreateUser", "testCreatePass", "testCreateEmail@test.com");

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
    public void createUserPositive() {
        // make sure no exception is thrown when creating user
        assertDoesNotThrow(() -> dataAccess.createUser(testUser));

        // make sure user created is in database
        UserData retrievedUser = assertDoesNotThrow(() -> dataAccess.getUser(testUser.username()));
        assertNotNull(retrievedUser);
        assertEquals(testUser.username(), retrievedUser.username());
        assertEquals(testUser.email(), retrievedUser.email());

        assertNotNull(retrievedUser.password());
    }

    @Test
    public void createUserNegative() {
        assertDoesNotThrow(() -> dataAccess.createUser(testUser));

        // try to create same user again, should throw error
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(testUser));
    }

}