package ui;

import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isLoggedIn = false;
    private static final LoginUI loginUI = new LoginUI();
    private static final RegisterUI registerUI = new RegisterUI();
    private static final LogoutUI logoutUI = new LogoutUI();
    private static final CreateGameUI createGameUI = new CreateGameUI();
    private static final ListGamesUI listGamesUI = new ListGamesUI();
    private static String userAuthToken;
    private static int gameID;


    public void clientStart() {
        System.out.println("♕Welcome to 240 chess. Type Help to get started.♕");

        // should prob clear database each time? idk

        // runs until user quits
        while (true) {
            if (!isLoggedIn) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else {
                System.out.print("[LOGGED_IN] >>> ");
            }
            String input = scanner.nextLine().trim().toLowerCase();  // make sure input works for different types
            inputHandler(input);

        }
    }

    private static void inputHandler(String input) {
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
                    userAuthToken = loginUI.loginUI(parts[1], parts[2]);  // grab authToken when logging in user
                    if (userAuthToken != null) {
                        isLoggedIn = true;  // make sure to only change UI display if the user is actually logged in
                    }
                } else {
                    System.out.println("must have syntax if not already logged in: login <USERNAME> <PASSWORD>");
                }
                break;
            case "register":
                if(parts.length == 4 && !isLoggedIn) {
                    registerUI.registerUI(parts[1], parts[2], parts[3]);
                } else {
                    System.out.println("must have syntax if not already logged in: register <USERNAME> <PASSWORD> <EMAIL>");
                }
                break;

            //// PostLogin Commands ////
            case "logout":
                if (userAuthToken != null) {
                    logoutUI.logoutUI(userAuthToken);  // should only work if user already logged in
                    isLoggedIn = false;
                } else {
                    System.out.println("must log in user first");
                }
                break;
            case "create":
                if (parts.length == 2 && isLoggedIn) {
                    gameID = createGameUI.createGameUI(userAuthToken, parts[1]);
                } else {
                    System.out.println("must be logged in and have syntax: create <NAME>");
                }
                break;
            case "list":
                if (isLoggedIn) {
                    listGamesUI.listGamesUI(userAuthToken);
                } else {
                    System.out.println("must log in first to view game list");
                }
                break;

            // add more cases

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

}