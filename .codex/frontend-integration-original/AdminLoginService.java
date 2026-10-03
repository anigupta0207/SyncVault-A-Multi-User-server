package Admin;

import java.sql.*;

public class AdminLoginService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syncvault";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD =
            "Animesh9889";

    public boolean login(String email, String password) {

        String query =
                "SELECT u.user_id, u.name, u.password, r.role_name " +
                        "FROM Users u " +
                        "JOIN Roles r ON u.role_id = r.role_id " +
                        "WHERE u.email = ? " +
                        "AND u.status = 'active'";

        try (Connection conn =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    return false;
                }

                String storedPassword = rs.getString("password");
                String role = rs.getString("role_name");

                // its temporary
                // hashing will be add later right now we are checking the code
                if (!storedPassword.equals(password)) {
                    return false;
                }

                // This service is espically for admin
                if (!role.equalsIgnoreCase("admin")) {
                    return false;
                }

                System.out.println("Admin authenticated:");
                System.out.println("User ID : " + rs.getInt("user_id"));
                System.out.println("Name    : " + rs.getString("name"));
                System.out.println("Role    : " + role);

                return true;
            }

        } catch (SQLException e) {

            System.out.println("Database error during login.");
            e.printStackTrace();

            return false;
        }
    }
}