package ui;

import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isLoggedIn = false;
    private static final LoginUI loginUI = new LoginUI();
    private static final RegisterUI registerUI = new RegisterUI();


    public void clientStart() {
        System.out.println("♕Welcome to 240 chess. Type Help to get started.♕");

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
            case "help":
                displayHelp();
                break;
            case "quit":
                System.exit(0); // exit UI
                break;
            case "login":
                if (parts.length == 3) {
                    loginUI.loginUI(parts[1], parts[2]);
                    isLoggedIn = true;

                } else {
                    System.out.println("must have syntax: login <USERNAME> <PASSWORD>");
                }
                break;
            case "register":
                if(parts.length == 4) {
                    registerUI.registerUI(parts[1], parts[2], parts[3]);
                } else {
                    System.out.println("must have syntax: register <USERNAME> <PASSWORD> <EMAIL>");
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