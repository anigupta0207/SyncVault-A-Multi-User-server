import java.sql.*;

public class DBTest {

    // Change these to match your local MySQL setup.
    private static final String URL = "jdbc:mysql://localhost:3306/syncvault";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Animesh9889";

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD)) {

            System.out.println("Connected to syncvault database successfully.\n");

            // ---- READ TEST: confirm the seed data is there ----
            String selectQuery =
                    "SELECT u.user_id, u.name, u.email, r.role_name " +
                            "FROM Users u JOIN Roles r ON u.role_id = r.role_id";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectQuery)) {

                System.out.println("Current users:");
                while (rs.next()) {
                    System.out.printf("  [%d] %s (%s) - role: %s%n",
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role_name"));
                }
            }

            // ---- WRITE TEST: insert a new user, confirm it commits ----
            String insertQuery =
                    "INSERT INTO Users (name, email, password, role_id) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, "Test Student");
                pstmt.setString(2, "test.student@syncvault.com");
                pstmt.setString(3, "hashed_password_test");
                pstmt.setInt(4, 3); // role_id 3 = student

                int rowsInserted = pstmt.executeUpdate();
                System.out.println("\nRows inserted: " + rowsInserted);
            }

            // ---- Re-read to confirm the insert actually persisted ----
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectQuery)) {

                System.out.println("\nUsers after insert:");
                while (rs.next()) {
                    System.out.printf("  [%d] %s%n", rs.getInt("user_id"), rs.getString("name"));
                }
            }

            System.out.println("\nStep 2 verified: Java <-> MySQL read and write both work.");

        } catch (SQLException e) {
            System.out.println("Database connection or query failed.");
            e.printStackTrace();
        }
    }
}