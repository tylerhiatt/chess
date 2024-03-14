package ui;

import chess.ChessGame;

import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isLoggedIn = false;
    private static final LoginUI loginUI = new LoginUI();
    private static final RegisterUI registerUI = new RegisterUI();
    private static final LogoutUI logoutUI = new LogoutUI();
    private static final CreateGameUI createGameUI = new CreateGameUI();
    private static final ListGamesUI listGamesUI = new ListGamesUI();
    private static final JoinGameUI joinGameUI = new JoinGameUI();
    private static String userAuthToken;  // used for multiple client methods

    public void clientStart(int port) {
        System.out.println("♕Welcome to 240 chess. Type Help to get started.♕");

        // should prob clear database from time to time? idk

        // runs until user quits
        while (true) {
            if (!isLoggedIn) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else {
                System.out.print("[LOGGED_IN] >>> ");
            }
            String input = scanner.nextLine().trim().toLowerCase();  // make sure input works for different types
            inputHandler(input, port);

        }
    }

    private static void inputHandler(String input, int port) {
        String[] parts = input.split(" ");
        String command = parts[0].toLowerCase();

        switch(command) {
            //// Generic UI Commands ////
            case "help":
                displayHelp();
                break;
            case "quit":
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
                    registerUI.registerUI(port, parts[1], parts[2], parts[3]);
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
                        // print initial boards
                        printGames();

                    }

                } else {
                    System.out.println("must be logged in and have syntax: join <ID> [WHITE|BLACK|<empty>]");
                }
                break;
            case "observe":
                if (parts.length == 2 && isLoggedIn) {
                    joinGameUI.joinGameUI(port, userAuthToken, Integer.parseInt(parts[1]), null);  // playerColor = null means observer
                    // print initial boards
                    printGames();

                } else {
                    System.out.println("must be logged in and have syntax: observe <ID>");
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
        } else {
            System.out.println("  create <NAME> - a game");
            System.out.println("  list - games");
            System.out.println("  join <ID> [WHITE|BLACK|<empty>] - a game");
            System.out.println("  observe <ID> - a game");
            System.out.println("  logout - when you are done");
            System.out.println("  quit - playing chess");
            System.out.println("  help - with possible commands");
        }
    }

    private static void printGames() {
        // print black then white
        GameplayUI.printBoardBlackOrientation();
        System.out.println();
        GameplayUI.printBoardWhiteOrientation();
    }

}