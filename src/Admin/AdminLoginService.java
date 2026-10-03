package Admin;

import auth.AuthenticatedUser;
import java.sql.*;

public class AdminLoginService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syncvault";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD =
            "Animesh9889";

    public boolean login(String email, String password) {

        AuthenticatedUser user;
        try {
            user = authenticateUser(email, password);
        } catch (SQLException e) {
            System.out.println("Database error during login.");
            e.printStackTrace();
            return false;
        }
        if (user == null || !user.getRole().equalsIgnoreCase("admin")) return false;

        System.out.println("Admin authenticated:");
        System.out.println("User ID : " + user.getUserId());
        System.out.println("Name    : " + user.getName());
        System.out.println("Role    : " + user.getRole());
        return true;
    }

    /** Authenticates any active role using the MySQL instance available to this server. */
    public AuthenticatedUser authenticateUser(String email, String password) throws SQLException {

        String query =
                "SELECT u.user_id, u.name, u.email, r.role_name " +
                        "FROM Users u " +
                        "JOIN Roles r ON u.role_id = r.role_id " +
                        "WHERE u.email = ? " +
                        "AND u.password = ? " +
                        "AND u.status = 'active'";

        try (Connection conn =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }
                String role = rs.getString("role_name");
                return new AuthenticatedUser(rs.getInt("user_id"), rs.getString("name"),
                        rs.getString("email"), role);
            }
        }
    }
}
