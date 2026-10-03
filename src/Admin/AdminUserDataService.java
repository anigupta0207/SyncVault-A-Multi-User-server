package Admin;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminUserDataService {

    public static class UserInfo {

        private final int userId;
        private final String name;
        private final String email;
        private final String role;
        private final String status;

        public UserInfo(
                int userId,
                String name,
                String email,
                String role,
                String status
        ) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
        }

        public int getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getStatus() {
            return status;
        }
    }

    public List<UserInfo> getAllUsers() throws SQLException {

        List<UserInfo> users = new ArrayList<>();

        String query =
                "SELECT u.user_id, u.name, u.email, " +
                        "r.role_name, u.status " +
                        "FROM Users u " +
                        "JOIN Roles r ON u.role_id = r.role_id " +
                        "ORDER BY u.user_id";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query);

                ResultSet rs =
                        pstmt.executeQuery()
        ) {

            while (rs.next()) {

                users.add(
                        new UserInfo(
                                rs.getInt("user_id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("role_name"),
                                rs.getString("status")
                        )
                );
            }
        }

        return users;
    }
}