package clientTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLDataAccess;
import org.junit.jupiter.api.*;
import server.Server;
import ui.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static Client serverFacade;
    static MySQLDataAccess dataAccess;
    static int port;
    private final LogoutUI logoutUI = new LogoutUI();
    private final CreateGameUI createGameUI = new CreateGameUI();
    private final LoginUI loginUI = new LoginUI();
    private final JoinGameUI joinGameUI = new JoinGameUI();
    private final ListGamesUI listGamesUI = new ListGamesUI();

    @BeforeAll
    public static void setup() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        // get dataAccess
        dataAccess = MySQLDataAccess.getInstance();

        // start client
        serverFacade = new Client();
        serverFacade.inputHandler("logout", port);
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException { dataAccess.clear(); }

    @AfterEach
    void logoutServerFacade() {
        serverFacade.inputHandler("logout", port);
    }

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

        var originalOut = System.out;
        var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // try to register same user twice, (logout in between to do so) second one should fail
        serverFacade.inputHandler(simulatedInput, port);
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

    @Test
    public void testLogoutPositive() {
        String registerInput = "register test test test";
        String loginInput = "login test test";
        String logoutInput = "logout";

        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // register user then login
        serverFacade.inputHandler(registerInput, port);
        serverFacade.inputHandler(loginInput, port);
        serverFacade.inputHandler(logoutInput, port);

        // make sure 200 response is printed out
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("Logged out user"));

    }

    @Test
    public void testLogoutNegative() {
        String registerInput = "register test test test";
        String loginInput = "login test test";
        String invalidAuthToken = "asdflkjasdf";

        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // logout user with invalid authToken
        serverFacade.inputHandler(registerInput, port);
        serverFacade.inputHandler(loginInput, port);
        logoutUI.logoutUI(port, invalidAuthToken);

        // should fail with invalid auth Token
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("{\"message\":\"Error: unauthorized\"}"));

    }

    @Test
    public void testCreateGamePositive() {
        String registerInput = "register test test test";
        String loginInput = "login test test";
        String createGameInput = "create testGame";

        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // register user, login, then create game
        serverFacade.inputHandler(registerInput, port);
        serverFacade.inputHandler(loginInput, port);
        serverFacade.inputHandler(createGameInput, port);

        // make sure 200 response is printed out
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("Game testGame created successfully"));

    }

    @Test
    public void testCreateGameNegative() {
        String registerInput = "register test test test";
        String loginInput = "login test test";
        String createGameInput = "create badGame";
        String invalidAuthToken = "asdflkjasdf";

        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // create game with invalid authToken
        serverFacade.inputHandler(registerInput, port);
        serverFacade.inputHandler(loginInput, port);
        createGameUI.createGameUI(port, invalidAuthToken, createGameInput);

        // should fail with invalid auth Token
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("{\"message\":\"Error: unauthorized\"}"));
    }

    @Test
    public void testJoinGamePositive() {
        String registerInput = "register test test test";

        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // register user, login, create game then join game with gameID
        serverFacade.inputHandler(registerInput, port);
        String authToken = loginUI.loginUI(port, "test", "test");
        serverFacade.inputHandler("login test test", port);
        int gameID = createGameUI.createGameUI(port, authToken, "testGame");
        serverFacade.inputHandler("join " + gameID + " WHITE", port);

        // make sure 200 response is printed out
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("Joined Game with gameID"));
    }

    @Test
    public void testJoinGameNegative() {
        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // register user, login, create game, then try to join game with invalid authToken
        serverFacade.inputHandler("register test test test", port);
        String authToken = loginUI.loginUI(port, "test", "test");
        serverFacade.inputHandler("login test test", port);
        int gameID = createGameUI.createGameUI(port, authToken, "testGame");
        joinGameUI.joinGameUI(port, "asdfasdf", gameID, "WHITE");

        // should fail with invalid authToken
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("{\"message\":\"Error: unauthorized\"}"));
    }

    @Test
    public void testListGamesPositive() {
        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // register user, login, create game then list game
        serverFacade.inputHandler("register test test test", port);
        serverFacade.inputHandler("login test test", port);
        serverFacade.inputHandler("create testGame", port);
        serverFacade.inputHandler("list", port);

        // make sure part of 200 response is printed out
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("White Player"));
    }

    @Test
    public void testListGamesNegative() {
        final var originalOut = System.out;
        final var getOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(getOutput));

        // register user, login, create game then list game with invalid authToken
        serverFacade.inputHandler("register test test test", port);
        serverFacade.inputHandler("login test test", port);
        serverFacade.inputHandler("create testGame", port);
        listGamesUI.listGamesUI(port, "asdfasfdasf");

        // should fail with invalid authToken
        System.setOut(originalOut);
        assertTrue(getOutput.toString().contains("{\"message\":\"Error: unauthorized\"}"));
    }

}