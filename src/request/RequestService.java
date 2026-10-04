package request;

import java.sql.Connection;
import db.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RequestService {



    // CREATE REQUEST

    public boolean createRequest(
            int userId,
            Integer fileId,
            String requestType,
            int priority) {

        String query =
                "INSERT INTO Requests " +
                        "(user_id, file_id, request_type, status, priority) " +
                        "VALUES (?, ?, ?, 'pending', ?)";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query)
        ) {

            pstmt.setInt(1, userId);

            if (fileId == null) {

                pstmt.setNull(
                        2,
                        Types.INTEGER
                );

            } else {

                pstmt.setInt(2, fileId);
            }

            pstmt.setString(3, requestType);
            pstmt.setInt(4, priority);

            int rowsInserted =
                    pstmt.executeUpdate();

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


    // GET PENDING REQUESTS

    public List<Request> getPendingRequests() {

        List<Request> requests =
                new ArrayList<>();

        String query =
                "SELECT request_id, user_id, file_id, " +
                        "request_type, status, priority, request_time " +
                        "FROM Requests " +
                        "WHERE status = 'pending' " +
                        "ORDER BY request_time ASC, request_id ASC";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query);

                ResultSet rs =
                        pstmt.executeQuery()
        ) {

            while (rs.next()) {

                Integer fileId = null;

                int databaseFileId =
                        rs.getInt("file_id");

                if (!rs.wasNull()) {
                    fileId = databaseFileId;
                }

                Request request =
                        new Request(
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

    // VIEW PENDING REQUESTS
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

            System.out.println(
                    "=============================================="
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


  // here claim mean where the request are pending remove it pending

    public boolean claimRequest(int requestId) {

        String query =
                "UPDATE Requests " +
                        "SET status = 'processing' " +
                        "WHERE request_id = ? " +
                        "AND status = 'pending'";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query)
        ) {

            pstmt.setInt(1, requestId);

            int rowsUpdated =
                    pstmt.executeUpdate();

            if (rowsUpdated == 1) {

                System.out.println(
                        "Request "
                                + requestId
                                + " successfully claimed."
                );

                return true;
            }

            System.out.println(
                    "Request "
                            + requestId
                            + " was already claimed."
            );

            return false;

        } catch (SQLException e) {

            System.out.println(
                    "Database error while claiming request."
            );

            e.printStackTrace();

            return false;
        }
    }


    // here we simply setting the request to completed

    public boolean updateRequestStatus(
            int requestId,
            String status) {

        String query =
                "UPDATE Requests " +
                        "SET status = ?, " +
                        "processed_time = CASE " +
                        "WHEN ? = 'completed' " +
                        "THEN CURRENT_TIMESTAMP " +
                        "ELSE processed_time END " +
                        "WHERE request_id = ?";

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pstmt =
                        conn.prepareStatement(query)
        ) {

            pstmt.setString(1, status);
            pstmt.setString(2, status);
            pstmt.setInt(3, requestId);

            int rowsUpdated =
                    pstmt.executeUpdate();

            if (rowsUpdated == 1) {

                return true;
            }

            return false;

        } catch (SQLException e) {

            System.out.println(
                    "Database error while updating request."
            );

            e.printStackTrace();

            return false;
        }
    }
}