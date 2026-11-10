package client;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while (running) {
            System.out.println("♕ 240 Chess Client");
            System.out.println("1. Help");
            System.out.println("2. Quit");
            System.out.println("3. Login");
            System.out.println("4. Register");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.println("You selected: Help \n");
            } else if (choice.equals("2")) {
                System.out.println("Goodbye");
                running = false;
            } else if (choice.equals("3")) {
                System.out.println("You selected: Login \n");
            } else if (choice.equals("4")) {
                System.out.println("You selected: Register \n");
            } else {
                System.out.println("Invalid choice.\n");
            }

        }
    }
}
