package client;

import model.AuthData;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ServerFacade facade = new ServerFacade(8080);

        boolean running = true;

        while (running) {
            System.out.println("♕ Welcome to 240 Chess ♕");
            System.out.println("1. Help");
            System.out.println("2. Quit");
            System.out.println("3. Login");
            System.out.println("4. Register");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.println("You selected: Help \n");
                System.out.println("1. Help - Show available commands.");
                System.out.println("2. Quit - Exit the chess client.");
                System.out.println("3. Login - Log into your account.");
                System.out.println("4. Register - Create a new account. \n");
            } else if (choice.equals("2")) {
                System.out.println("Goodbye");
                running = false;
            } else if (choice.equals("3")) {
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                try {
                    AuthData auth = facade.login(username, password);

                    System.out.println("You successfully logged in!");
                    System.out.println("Welcome, " + auth.username() + "!");
                    postLoginMenu(scanner, facade, auth);

                } catch (Exception e) {
                    System.out.println("Login failed. Please check your username and password.");
                }
            } else if (choice.equals("4")) {
                System.out.print("Choose a username: ");
                String username = scanner.nextLine();

                System.out.print("Choose a password: ");
                String password = scanner.nextLine();

                System.out.print("Enter your email: ");
                String email = scanner.nextLine();

                try {
                    AuthData auth = facade.register(username, password, email);

                    System.out.println("Registration successful!");
                    System.out.println("Welcome, " + auth.username() + "!");
                    postLoginMenu(scanner, facade, auth);

                } catch (Exception e) {
                    System.out.println("Registration failed. Username may already be taken.");
                }
            } else {
                System.out.println("Invalid choice.\n");
            }
        }
    }
    private static void postLoginMenu(Scanner scanner, ServerFacade facade, AuthData auth) {

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n♕ Chess Menu ♕");
            System.out.println("1. Help");
            System.out.println("2. Logout");
            System.out.println("3. Create Game");
            System.out.println("4. List Games");
            System.out.println("5. Play Game");
            System.out.println("6. Observe Game");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\nAvailable Commands:");
                    System.out.println("1. Help - Show available commands.");
                    System.out.println("2. Logout - Log out of your account.");
                    System.out.println("3. Create Game - Create a new chess game.");
                    System.out.println("4. List Games - View available games.");
                    System.out.println("5. Play Game - Join a game.");
                    System.out.println("6. Observe Game - Watch a game.");
                    break;

                case "2":
                    try {
                        facade.logout(auth.authToken());
                        System.out.println("You successfully logged out!");
                        loggedIn = false;
                    } catch (Exception e) {
                        System.out.println("Logout failed. Please try again.");
                    }
                    break;

                case "3":
                    System.out.print("Enter a name for your game: ");
                    String gameName = scanner.nextLine();

                    try {
                        facade.createGame(auth.authToken(), gameName);
                        System.out.println("Game created successfully!");
                    } catch (Exception e) {
                        System.out.println("Could not create game. Please try again.");
                    }
                    break;

                case "4":
                    System.out.println("List Games");
                    break;

                case "5":
                    System.out.println("Play Game");
                    break;

                case "6":
                    System.out.println("Observe Game");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
