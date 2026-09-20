package Admin;

import java.sql.*;

public class AdminUserService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syncvault";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD =
            "Animesh9889";


    // VIEW USERS

    public void viewUsers() {

        String query =
                "SELECT u.user_id, u.name, u.email, " +
                        "u.status, u.created_at, r.role_name " +
                        "FROM Users u " +
                        "JOIN Roles r ON u.role_id = r.role_id " +
                        "ORDER BY u.user_id";

        try (Connection conn =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);

             PreparedStatement pstmt =
                     conn.prepareStatement(query);

             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n==============================================");
            System.out.println("              ALL SYNCVAULT USERS");
            System.out.println("==============================================");

            System.out.printf(
                    "%-5s %-20s %-30s %-12s %-10s%n",
                    "ID", "NAME", "EMAIL", "ROLE", "STATUS"
            );

            System.out.println(
                    "--------------------------------------------------------------------------"
            );

            while (rs.next()) {

                System.out.printf(
                        "%-5d %-20s %-30s %-12s %-10s%n",
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role_name"),
                        rs.getString("status")
                );
            }

            System.out.println(
                    "--------------------------------------------------------------------------"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database error while retrieving users."
            );

            e.printStackTrace();
        }
    }

    // User creation  part  -

    public boolean createUser(
            String name,
            String email,
            String password,
            String roleName) {

        String roleQuery =
                "SELECT role_id FROM Roles WHERE role_name = ?";

        String insertQuery =
                "INSERT INTO Users (name, email, password, role_id) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD)) {

            // searching od role id
            int roleId;

            try (PreparedStatement roleStmt =
                         conn.prepareStatement(roleQuery)) {

                roleStmt.setString(
                        1,
                        roleName.toLowerCase()
                );

                try (ResultSet rs =
                             roleStmt.executeQuery()) {

                    if (!rs.next()) {

                        System.out.println("Invalid role.");
                        return false;
                    }

                    roleId = rs.getInt("role_id");
                }
            }


            // Insertion of user
            try (PreparedStatement userStmt =
                         conn.prepareStatement(insertQuery)) {

                userStmt.setString(1, name);
                userStmt.setString(2, email);
                userStmt.setString(3, password);
                userStmt.setInt(4, roleId);

                int rowsInserted =
                        userStmt.executeUpdate();

                if (rowsInserted == 1) {

                    System.out.println(
                            "\nUser created successfully."
                    );

                    return true;
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            System.out.println(
                    "\nUser creation failed: email already exists."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database error while creating user."
            );

            e.printStackTrace();
        }

        return false;
    }

    // deactive the user <the problem is we cannot remove the user complete from the table >

    public boolean deactivateUser(int userId) {

        String query =
                "UPDATE Users " +
                        "SET status = 'inactive' " +
                        "WHERE user_id = ? " +
                        "AND status = 'active' " +
                        "AND user_id != 1";

        try (Connection conn =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated == 1) {

                System.out.println(
                        "\nUser deactivated successfully."
                );

                return true;
            }

            System.out.println(
                    "\nUser could not be deactivated."
            );

            return false;

        } catch (SQLException e) {

            System.out.println(
                    "Database error while deactivating user."
            );

            e.printStackTrace();

            return false;
        }
    }
}