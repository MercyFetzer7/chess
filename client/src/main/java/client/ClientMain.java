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
                System.out.print("Choose an option: ");
                choice = scanner.nextLine();
            }
            if (choice.equals("2")) {
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

                } catch (Exception e) {
                    System.out.println("Login failed. Please check your username and password.");
                }
            } else if (choice.equals("4")) {
                System.out.println("You selected: Register \n");
            } else {
                System.out.println("Invalid choice.\n");
            }

        }
    }
}
