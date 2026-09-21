package request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syncvault";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD =
            "Animesh9889";


    // ==========================================
    // CREATE REQUEST
    // ==========================================

    public boolean createRequest(
            int userId,
            Integer fileId,
            String requestType,
            int priority) {

        String query =
                "INSERT INTO Requests " +
                        "(user_id, file_id, request_type, status, priority) " +
                        "VALUES (?, ?, ?, 'pending', ?)";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD);

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            if (fileId == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, fileId);
            }

            pstmt.setString(3, requestType);
            pstmt.setInt(4, priority);

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted == 1) {

                System.out.println(
                        "Request created successfully."
                );

                return true;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database error while creating request."
            );

            e.printStackTrace();
        }

        return false;
    }


    // ==========================================
    // GET PENDING REQUESTS
    // ==========================================

    public List<Request> getPendingRequests() {

        List<Request> requests = new ArrayList<>();

        String query =
                "SELECT request_id, user_id, file_id, " +
                        "request_type, status, priority, request_time " +
                        "FROM Requests " +
                        "WHERE status = 'pending' " +
                        "ORDER BY request_time ASC";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD);

             PreparedStatement pstmt =
                     conn.prepareStatement(query);

             ResultSet rs =
                     pstmt.executeQuery()) {

            while (rs.next()) {

                Integer fileId = null;

                int databaseFileId =
                        rs.getInt("file_id");

                if (!rs.wasNull()) {
                    fileId = databaseFileId;
                }

                Request request = new Request(
                        rs.getInt("request_id"),
                        rs.getInt("user_id"),
                        fileId,
                        rs.getString("request_type"),
                        rs.getString("status"),
                        rs.getInt("priority"),
                        rs.getString("request_time")
                );

                requests.add(request);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database error while retrieving requests."
            );

            e.printStackTrace();
        }

        return requests;
    }


    // ==========================================
    // VIEW PENDING REQUESTS
    // ==========================================

    public void viewPendingRequests() {

        List<Request> requests =
                getPendingRequests();

        System.out.println(
                "\n=============================================="
        );

        System.out.println(
                "             PENDING REQUESTS"
        );

        System.out.println(
                "=============================================="
        );

        if (requests.isEmpty()) {

            System.out.println(
                    "No pending requests."
            );

            return;
        }

        for (Request request : requests) {

            System.out.println(request);
        }

        System.out.println(
                "=============================================="
        );
    }
    // ==========================================
// UPDATE REQUEST STATUS
// ==========================================

    public boolean updateRequestStatus(
            int requestId,
            String status) {

        String query =
                "UPDATE Requests " +
                        "SET status = ?, " +
                        "processed_time = CASE " +
                        "WHEN ? = 'completed' THEN CURRENT_TIMESTAMP " +
                        "ELSE processed_time END " +
                        "WHERE request_id = ?";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD);

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setString(2, status);
            pstmt.setInt(3, requestId);

            int rowsUpdated =
                    pstmt.executeUpdate();

            return rowsUpdated == 1;

        } catch (SQLException e) {

            System.out.println(
                    "Database error while updating request."
            );

            e.printStackTrace();

            return false;
        }
    }
}