package Admin;

import java.util.Scanner;

public class AdminLogin {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("---------------------------------");
        System.out.println("       SyncVault Admin Login");
        System.out.println("---------------------------------");

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        AdminLoginService loginService =
                new AdminLoginService();

        boolean success =
                loginService.login(email, password);

        if (success) {

            System.out.println("\nLogin successful.");
            System.out.println("Welcome to SyncVault Admin.");

            AdminMenu adminMenu = new AdminMenu();

            adminMenu.showMenu();

        } else {

            System.out.println("\nLogin failed.");
            System.out.println(
                    "Invalid credentials or insufficient privileges."
            );
        }

        scanner.close();
    }
}