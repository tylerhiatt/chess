package clientTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import org.junit.jupiter.api.*;
import server.Server;
import ui.Client;
import ui.RegisterUI;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static Client serverFacade;
    static MySQLDataAccess dataAccess;
    static int port;

    @BeforeAll
    public static void setup() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        // get dataAccess
        dataAccess = MySQLDataAccess.getInstance();

        // start client
        serverFacade = new Client();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException { dataAccess.clear(); }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPositive() {
        String simulatedInput = "register test test test";

        // since output is in terminal, verify that output matches what it should
        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // test register user
        serverFacade.inputHandler(simulatedInput, port);

        // make sure 200 response is printed out
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("Registered new user test"));

    }

    @Test
    public void testRegisterNegative() {
        String simulatedInput = "register test test test";
        String logoutInput = "logout";

        var originalOut = System.out;
        var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // try to register same user twice, (logout in between to do so) second one should fail
        serverFacade.inputHandler(simulatedInput, port);
        serverFacade.inputHandler(logoutInput, port);
        serverFacade.inputHandler(simulatedInput, port);

        // should fail with 403 error
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("{\"message\":\"Error: already taken\"}"));
    }

    @Test
    public void testLoginPositive() {
        String registerInput = "register test test test";
        String loginInput = "login test test";

        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // register user then login
        serverFacade.inputHandler(registerInput, port);
        serverFacade.inputHandler(loginInput, port);
        serverFacade.inputHandler("logout", port);  // as to not affect other tests

        // make sure 200 response is printed out
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("Logged in as test"));

    }

    @Test
    public void testLoginNegative() {
        String registerInput = "register test test test";
        String loginInput= "login test wrongPassword";

        var originalOut = System.out;
        var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // try to log in with wrong password, should throw 401 error
        serverFacade.inputHandler(registerInput, port);
        serverFacade.inputHandler(loginInput, port);

        // should fail with 403 error
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("{\"message\":\"Error: unauthorized\"}"));
    }



}