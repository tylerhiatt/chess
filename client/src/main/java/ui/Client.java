package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import ui.websocket.WebSocketClient;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.LEAVE;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isLoggedIn = false;
    private static boolean isGameplay = false;
    private static final LoginUI loginUI = new LoginUI();
    private static final RegisterUI registerUI = new RegisterUI();
    private static final LogoutUI logoutUI = new LogoutUI();
    private static final CreateGameUI createGameUI = new CreateGameUI();
    private static final ListGamesUI listGamesUI = new ListGamesUI();
    private static final JoinGameUI joinGameUI = new JoinGameUI();
    private static String userAuthToken;  // used for multiple client methods
    private WebSocketClient webSocketClient; // websocket client to establish connection
    private int gameplayGameID = 0;

    public void clientStart(int port) {
        System.out.println("♕Welcome to 240 chess. Type Help to get started.♕");

        // should prob clear database from time to time? idk

        // runs until user quits
        while (true) {
            if (!isLoggedIn) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else if (!isGameplay) {
                System.out.print("[LOGGED_IN] >>> ");
            } else {
                System.out.print("[GAMEPLAY] >>> ");
            }
            String input = scanner.nextLine().trim().toLowerCase();  // make sure input works for different types
            inputHandler(input, port);

        }
    }

    public void inputHandler(String input, int port) {
        String[] parts = input.split(" ");
        String command = parts[0].toLowerCase();

        switch(command) {
            //// Generic UI Commands ////
            case "help":
                displayHelp();
                break;
            case "quit":
                // close websocket connection if opened
                try {
                    if (webSocketClient != null) {
                        webSocketClient.closeSession();
                    }
                } catch (IOException e) {
                    System.err.println("Error closing WebSocket connection: " + e.getMessage());
                }
                System.exit(0); // exit UI and stop server
                break;

            //// PreLogin Commands ////
            case "login":
                if (parts.length == 3 && !isLoggedIn) {
                    userAuthToken = loginUI.loginUI(port, parts[1], parts[2]);  // grab authToken when logging in user
                    if (userAuthToken != null) {
                        isLoggedIn = true;  // make sure to only change UI display if the user is actually logged in
                    }
                } else {
                    System.out.println("must have syntax if not already logged in: login <USERNAME> <PASSWORD>");
                }
                break;
            case "register":
                if(parts.length == 4 && !isLoggedIn) {
                    String newAuthToken = registerUI.registerUI(port, parts[1], parts[2], parts[3]);
                    if (newAuthToken != null) {
                        userAuthToken = newAuthToken;
                        isLoggedIn = true;
                    }

                } else {
                    System.out.println("must have syntax if not already logged in: register <USERNAME> <PASSWORD> <EMAIL>");
                }
                break;

            //// PostLogin Commands ////
            case "logout":
                if (userAuthToken != null) {
                    logoutUI.logoutUI(port, userAuthToken);  // should only work if user already logged in
                    isLoggedIn = false;
                } else {
                    System.out.println("must log in user first");
                }
                break;
            case "create":
                if (parts.length == 2 && isLoggedIn) {
                    createGameUI.createGameUI(port, userAuthToken, parts[1]);
                } else {
                    System.out.println("must be logged in and have syntax: create <NAME>");
                }
                break;
            case "list":
                if (isLoggedIn) {
                    listGamesUI.listGamesUI(port, userAuthToken);
                } else {
                    System.out.println("must log in first to view game list");
                }
                break;
            case "join":
                if (parts.length >= 2 && parts.length <= 3 && isLoggedIn) {
                    String playerColor = "";
                    if (parts.length == 3) {
                        playerColor = parts[2].toUpperCase(); // WHITE OR BLACK, else stays empty
                    }

                    if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK") && !playerColor.isEmpty()) {
                        System.out.println("player color must be WHITE, BLACK, or leave empty");
                    } else {
                        joinGameUI.joinGameUI(port, userAuthToken, Integer.parseInt(parts[1]), playerColor);

                        // set player color to team color object
                        ChessGame.TeamColor teamColor = null;
                        if (playerColor.equals("WHITE")) {
                            teamColor = ChessGame.TeamColor.WHITE;
                        } else if (playerColor.equals("BLACK")) {
                            teamColor = ChessGame.TeamColor.BLACK;
                        }

                        // set gameplay ID to use for leaving and resigning game
                        gameplayGameID = Integer.parseInt(parts[1]);

                        // websocket connection and message
                        initializeWebSocketConnection("http://localhost:" + port);
                        webSocketClient.sendJoinPlayerCommand(userAuthToken, Integer.parseInt(parts[1]), teamColor);

                        // go to gameplay UI
                        isGameplay = true;

                        // print initial boards
                        printGames(playerColor);
                    }

                } else {
                    System.out.println("must be logged in and have syntax: join <ID> [WHITE|BLACK|<empty>]");
                }
                break;
            case "observe":
                if (parts.length == 2 && isLoggedIn) {
                    joinGameUI.joinGameUI(port, userAuthToken, Integer.parseInt(parts[1]), null);  // playerColor = null means observer

                    // websocket connection and message
                    initializeWebSocketConnection("http://localhost:" + port);
                    webSocketClient.sendJoinObserverCommand(userAuthToken, Integer.parseInt(parts[1]));

                    // set gameplay ID to use for leaving game
                    gameplayGameID = Integer.parseInt(parts[1]);

                    // switch to gameplay UI
                    isGameplay = true;

                    // print initial boards simply from white perspective
                    printGames("WHITE");

                } else {
                    System.out.println("must be logged in and have syntax: observe <ID>");
                }
                break;

            //// Gameplay commands ////
            case "redraw":
                // todo: handle using websocket message response to get game state
                printGames("WHITE");
                break;
            case "move":
                if (parts.length == 3) {
                    try {
                        // identify chess move
                        ChessPosition startPos = convertMoveToChessPos(parts[1]); // a2 becomes row = 2, col = 1
                        ChessPosition endPos = convertMoveToChessPos(parts[2]);
                        ChessMove move = new ChessMove(startPos, endPos, null);

                        // send make move command
                        webSocketClient.sendMakeMoveCommand(userAuthToken, gameplayGameID, move);
                    } catch (Exception e) {
                        System.out.println("Error processing move command");
                    }
                }
                break;
            case "highlight":
                if (parts.length == 2) {
                    // turn command into ChessPosition
                    ChessPosition pos = convertMoveToChessPos(parts[1]);
                }
                break;
            case "leave":
                // send websocket command to leave
                try {
                    webSocketClient.sendLeaveCommand(userAuthToken, gameplayGameID);
                    isGameplay = false;

                    // close websocket connection
                    webSocketClient.closeSession();
                } catch (IOException e) {
                    System.err.println("Error closing WebSocket connection: " + e.getMessage());
                }
                break;
            case "resign":
                // send websocket command to resign game
                try {
                    webSocketClient.sendResignCommand(userAuthToken, gameplayGameID);
                    isGameplay = false;
                    webSocketClient.closeSession();
                } catch (IOException e) {
                    System.err.println("Error closing WebSocket connection: " + e.getMessage());
                }
                break;

            default:
                System.out.println("Unknown command. Type 'help' to see available commands.");
                break;
        }
    }

    private static void displayHelp() {
        // pre login help display
        if (!isLoggedIn) {
            System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("  login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("  quit - playing chess");
            System.out.println("  help - with possible commands");

        // post login help display
        } else if (!isGameplay){
            System.out.println("  create <NAME> - a game");
            System.out.println("  list - games");
            System.out.println("  join <ID> [WHITE|BLACK|<empty>] - a game");
            System.out.println("  observe <ID> - a game");
            System.out.println("  logout - when you are done");
            System.out.println("  quit - playing chess");
            System.out.println("  help - with possible commands");

        // post login gameplay help display
        } else {
            System.out.println("  redraw - redraws the chess board");
            System.out.println("  move <START POS> <END POS> - input move with start and end position");
            System.out.println("  highlight <PIECE POS>- highlights legal moves specified piece can take");
            System.out.println("  leave <ID>- remove yourself from game, specify the gameID");
            System.out.println("  resign - forfeit the game");
            System.out.println("  help - with possible commands");
        }
    }

    private static void printGames(String playerColor) {
        if (playerColor.equals("WHITE")) {
            // print black then white
            GameplayUI.printBoardBlackOrientation();
            System.out.println();
            GameplayUI.printBoardWhiteOrientation();
        } else if (playerColor.equals("BLACK")) {
            // print white then black
            GameplayUI.printBoardWhiteOrientation();
            System.out.println();
            GameplayUI.printBoardBlackOrientation();
        }

    }

    private void initializeWebSocketConnection(String url) {
        try {
            this.webSocketClient = new WebSocketClient(url, this::handleWebsocketMessage);
            System.out.println("Websocket connection established");

        } catch (URISyntaxException | DeploymentException | IOException e) {
            System.err.println("Failed to establish WebSocket connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleWebsocketMessage(ServerMessage message) {
        //System.out.println("WebSocket message received: " + message);
    }

    private ChessPosition convertMoveToChessPos(String pos){
        // split up pos
        int col = pos.charAt(0) - 'a' + 1; // a maps to 1, b -> 2, etc.
        int row = pos.charAt(1) - '0';  // 1 stays 1, etc.
        return new ChessPosition(row, col);

    }

}