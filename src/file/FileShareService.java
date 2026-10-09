package file;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileShareService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syncvault";

    private static final String DB_USER =
            "root";

    private static final String DB_PASSWORD =
            "Animesh9889";


    // =====================================================
    // SHARE FILE
    // =====================================================

    public boolean shareFile(
            int fileId,
            int sharedBy,
            int sharedWith,
            String permission) throws SQLException {

        // Validate permission
        if (!permission.equalsIgnoreCase("view")
                && !permission.equalsIgnoreCase("modify")) {

            System.out.println(
                    "Invalid sharing permission."
            );

            return false;
        }

        // Check whether file exists and is active
        String fileQuery =
                "SELECT owner_id, status " +
                        "FROM Files " +
                        "WHERE file_id = ?";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD);

             PreparedStatement fileStmt =
                     conn.prepareStatement(fileQuery)) {

            fileStmt.setInt(1, fileId);

            try (ResultSet rs =
                         fileStmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println(
                            "File not found."
                    );
                    return false;
                }

                int ownerId =
                        rs.getInt("owner_id");

                String status =
                        rs.getString("status");

                if (!"active".equalsIgnoreCase(status)) {
                    System.out.println(
                            "File is not active."
                    );
                    return false;
                }

                // Only owner can share for now
                if (ownerId != sharedBy) {
                    System.out.println(
                            "User is not the owner of this file."
                    );
                    return false;
                }
            }
        }

        // Prevent duplicate share
        String duplicateQuery =
                "SELECT share_id " +
                        "FROM File_Shares " +
                        "WHERE file_id = ? " +
                        "AND shared_with = ?";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD);

             PreparedStatement duplicateStmt =
                     conn.prepareStatement(duplicateQuery)) {

            duplicateStmt.setInt(1, fileId);
            duplicateStmt.setInt(2, sharedWith);

            try (ResultSet rs =
                         duplicateStmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println(
                            "File is already shared with this user."
                    );
                    return false;
                }
            }
        }

        // Insert share
        String insertQuery =
                "INSERT INTO File_Shares " +
                        "(file_id, shared_by, shared_with, permission) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD);

             PreparedStatement stmt =
                     conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, fileId);
            stmt.setInt(2, sharedBy);
            stmt.setInt(3, sharedWith);
            stmt.setString(
                    4,
                    permission.toLowerCase()
            );

            return stmt.executeUpdate() == 1;
        }
    }


    // =====================================================
    // GET FILES SHARED WITH USER
    // =====================================================
    public boolean canDownload(int fileId, int userId)
            throws SQLException {

        String query =
                "SELECT f.owner_id " +
                        "FROM Files f " +
                        "WHERE f.file_id = ? " +
                        "AND LOWER(f.status) = 'active' " +
                        "AND (" +
                        "    f.owner_id = ? " +
                        "    OR EXISTS (" +
                        "        SELECT 1 FROM File_Shares fs " +
                        "        WHERE fs.file_id = f.file_id " +
                        "        AND fs.shared_with = ? " +
                        "        AND LOWER(fs.permission) IN ('view', 'modify')" +
                        "    )" +
                        ")";

        try (Connection conn = DriverManager.getConnection(
                URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, fileId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    public List<FileShare> getSharedFiles(
            int userId) throws SQLException {

        List<FileShare> shares =
                new ArrayList<>();

                String query =
                "SELECT fs.share_id, fs.file_id, fs.shared_by, " +
                        "fs.shared_with, fs.permission, fs.shared_at, " +
                        "f.file_name, f.file_size, f.file_type " +
                        "FROM File_Shares fs " +
                        "JOIN Files f ON fs.file_id = f.file_id " +
                        "WHERE fs.shared_with = ? " +
                        "AND f.status = 'active' " +
                        "ORDER BY fs.shared_at DESC";

        try (Connection conn =
                     DriverManager.getConnection(
                             URL,
                             DB_USER,
                             DB_PASSWORD);

             PreparedStatement stmt =
                     conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs =
                         stmt.executeQuery()) {

                while (rs.next()) {

                    shares.add(
                                    new FileShare(
                                            rs.getInt("share_id"),
                                            rs.getInt("file_id"),
                                            rs.getInt("shared_by"),
                                            rs.getInt("shared_with"),
                                            rs.getString("permission"),
                                            rs.getString("shared_at"),
                                            rs.getString("file_name"),
                                            rs.getLong("file_size"),
                                            rs.getString("file_type")
                                    )
                    );
                }
            }
        }

        return shares;
    }
}