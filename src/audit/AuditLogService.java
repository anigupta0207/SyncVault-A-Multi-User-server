
package audit;
import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class AuditLogService {

    private AuditLogService() {
    }

    public static void log(
            int userId,
            Integer fileId,
            String action,
            String description,
            String result
    ) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "A valid user ID is required for an audit record."
            );
        }

        String sql =
                "INSERT INTO Audit_Log " +
                        "(user_id, file_id, action_type, description, result) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            if (fileId == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, fileId);
            }

            ps.setString(3, action);
            ps.setString(4, description);
            ps.setString(5, result);

            ps.executeUpdate();
        }
    }

    public static List<String[]> getRecentLogs()
            throws SQLException {

        List<String[]> logs = new ArrayList<>();

        String sql =
                "SELECT a.log_id, a.timestamp, " +
                        "COALESCE(u.name, CONCAT('User #', a.user_id)) AS user_name, " +
                        "a.action_type, " +
                        "COALESCE(f.file_name, a.description, '-') AS resource, " +
                        "a.result " +
                        "FROM Audit_Log a " +
                        "LEFT JOIN Users u ON u.user_id = a.user_id " +
                        "LEFT JOIN Files f ON f.file_id = a.file_id " +
                        "ORDER BY a.timestamp DESC, a.log_id DESC " +
                        "LIMIT 200";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(new String[]{
                            rs.getString("timestamp"),
                            rs.getString("user_name"),
                            rs.getString("action_type"),
                            rs.getString("resource"),
                            rs.getString("result")
                    });
                }
            }
        }

        return logs;
    }

    public static List<String[]> getLogsForUser(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        List<String[]> logs = new ArrayList<>();

        String sql =
                "SELECT a.log_id, a.timestamp, " +
                        "COALESCE(u.name, CONCAT('User #', a.user_id)) AS user_name, " +
                        "a.action_type, " +
                        "COALESCE(f.file_name, a.description, '-') AS resource, " +
                        "a.result " +
                        "FROM Audit_Log a " +
                        "LEFT JOIN Users u ON u.user_id = a.user_id " +
                        "LEFT JOIN Files f ON f.file_id = a.file_id " +
                        "WHERE a.user_id = ? " +
                        "ORDER BY a.timestamp DESC, a.log_id DESC " +
                        "LIMIT 200";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(new String[]{
                            rs.getString("timestamp"),
                            rs.getString("user_name"),
                            rs.getString("action_type"),
                            rs.getString("resource"),
                            rs.getString("result")
                    });
                }
            }
        }

        return logs;
    }

}
