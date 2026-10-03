package Admin;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminStatsService {

    public int getTotalFiles() throws SQLException {

        String query = "SELECT COUNT(*) FROM Files";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query);

                ResultSet rs =
                        pstmt.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    public int getActiveUsers() throws SQLException {

        String query =
                "SELECT COUNT(*) FROM Users WHERE status = 'active'";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query);

                ResultSet rs =
                        pstmt.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    public int getPendingRequests() throws SQLException {

        String query =
                "SELECT COUNT(*) FROM Requests WHERE status = 'Pending'";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query);

                ResultSet rs =
                        pstmt.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }
}