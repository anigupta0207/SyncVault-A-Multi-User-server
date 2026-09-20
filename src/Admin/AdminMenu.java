package Admin;

import java.util.Scanner;

public class AdminMenu {

    private final Scanner scanner;
    private final AdminUserService userService;

    public AdminMenu() {

        scanner = new Scanner(System.in);
        userService = new AdminUserService();
    }

    public void showMenu() {

        while (true) {

            System.out.println("\n=================================");
            System.out.println("          SYNCVAULT ADMIN");
            System.out.println("=================================");
            System.out.println("1. View Users");
            System.out.println("2. Create User");
            System.out.println("3. Exit");
            System.out.println("=================================");

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":

                    userService.viewUsers();

                    break;


                case "2":

                    createUser();

                    break;


                case "3":

                    System.out.println("Logging out...");

                    return;


                default:

                    System.out.println("Invalid choice.");
            }
        }
    }


    // INput for user

    private void createUser() {

        System.out.println("\n=================================");
        System.out.println("          CREATE USER");
        System.out.println("=================================");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("\nAvailable roles:");
        System.out.println("1. Teacher");
        System.out.println("2. Student");

        System.out.print("Select role: ");

        String roleChoice = scanner.nextLine();

        String role;

        switch (roleChoice) {

            case "1":

                role = "teacher";

                break;


            case "2":

                role = "student";

                break;


            default:

                System.out.println("Invalid role selection.");

                return;
        }

        userService.createUser(
                name,
                email,
                password,
                role
        );
    }
}