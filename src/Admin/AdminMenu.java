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
            System.out.println("3. Deactivate User");
            System.out.println("4. Exit");
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

                    deactivateUser();

                    break;


                case "4":

                    System.out.println("Logging out...");

                    return;


                default:

                    System.out.println("Invalid choice.");
            }
        }
    }


    // DEACTIVATE USER

    private void deactivateUser() {

        System.out.println("\n=================================");
        System.out.println("        DEACTIVATE USER");
        System.out.println("=================================");

        System.out.print("Enter User ID: ");

        String input = scanner.nextLine();

        try {

            int userId = Integer.parseInt(input);

            userService.deactivateUser(userId);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid User ID. Please enter a number."
            );
        }
    }

    // CREATE USER

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


        // Role selection

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

                System.out.println(
                        "Invalid role selection."
                );

                return;
        }


        // Send data to service

        userService.createUser(
                name,
                email,
                password,
                role
        );
    }
}