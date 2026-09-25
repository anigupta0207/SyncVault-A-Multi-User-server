package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/syncvault";

    private static final String DB_USER =
            "root";

    private static final String DB_PASSWORD =
            "Animesh9889";

    // Server-side storage directory
    private static final String STORAGE_DIRECTORY =
            "storage";


    // INITIALIZE STORAGE

    public FileService() {

        try {

            Path storagePath =
                    Paths.get(STORAGE_DIRECTORY);

            if (!Files.exists(storagePath)) {

                Files.createDirectories(storagePath);

                System.out.println(
                        "SyncVault storage directory created."
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Could not create storage directory."
            );

            e.printStackTrace();
        }
    }

    // UPLOAD FILE

    public boolean uploadFile(String sourceFilePath, String fileName, int ownerId) {

        String insertQuery =
                "INSERT INTO Files " +
                        "(file_name, file_path, file_type, file_size, owner_id) " +
                        "VALUES (?, ?, ?, ?, ?)";

        String updatePathQuery =
                "UPDATE Files SET file_path = ? WHERE file_id = ?";

        Path sourceFile = Paths.get(sourceFilePath);

        if (!Files.exists(sourceFile)) {
            System.out.println("Source file does not exist.");
            return false;
        }

        long fileSize;

        try {
            fileSize = Files.size(sourceFile);
        } catch (IOException e) {
            System.out.println("Could not determine file size.");
            e.printStackTrace();
            return false;
        }

        try (Connection conn =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
             PreparedStatement insertStmt =
                     conn.prepareStatement(insertQuery,
                             Statement.RETURN_GENERATED_KEYS)) {

            // Temporary path. We will update it after getting file_id.
            insertStmt.setString(1, fileName);
            insertStmt.setString(2, "TEMP");
            insertStmt.setString(3, getFileType(fileName));
            insertStmt.setLong(4, fileSize);
            insertStmt.setInt(5, ownerId);

            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted != 1) {
                System.out.println("File metadata insertion failed.");
                return false;
            }

            int fileId;

            try (ResultSet rs = insertStmt.getGeneratedKeys()) {

                if (!rs.next()) {
                    System.out.println("Could not get generated file ID.");
                    return false;
                }

                fileId = rs.getInt(1);
            }

            // Create owner directory
            Path ownerDirectory =
                    Paths.get(STORAGE_DIRECTORY, String.valueOf(ownerId));

            if (!Files.exists(ownerDirectory)) {
                Files.createDirectories(ownerDirectory);
            }

            // Unique physical filename
            String uniqueFileName = fileId + "_" + fileName;

            Path destination =
                    ownerDirectory.resolve(uniqueFileName);

            // Copy physical file
            Files.copy(
                    sourceFile,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Update database with actual physical path
            try (PreparedStatement updateStmt =
                         conn.prepareStatement(updatePathQuery)) {

                updateStmt.setString(1, destination.toString());
                updateStmt.setInt(2, fileId);

                int updated = updateStmt.executeUpdate();

                if (updated != 1) {

                    Files.deleteIfExists(destination);

                    System.out.println("Could not update file path.");
                    return false;
                }
            }

            System.out.println("File uploaded successfully.");
            System.out.println("File ID: " + fileId);
            System.out.println("File Name: " + fileName);
            System.out.println("Stored At: " + destination);

            return true;

        } catch (SQLException e) {

            System.out.println("Database error during upload.");
            e.printStackTrace();
            return false;

        } catch (IOException e) {

            System.out.println("File error during upload.");
            e.printStackTrace();
            return false;
        }
    }
    // type of file

    private String getFileType(String fileName) {

        int lastDot = fileName.lastIndexOf('.');

        if (lastDot == -1 || lastDot == fileName.length() - 1) {

            return "unknown";
        }

        return fileName
                .substring(lastDot + 1)
                .toLowerCase();
    }

    // GET ALL FILES

    public List<FileInfo> getAllFiles() {

        List<FileInfo> files = new ArrayList<>();

        String query =
                "SELECT file_id, file_name, file_path, " +
                        "file_type, file_size, status, " +
                        "upload_date, owner_id " +
                        "FROM Files " +
                        "ORDER BY upload_date ASC";


        try (
                Connection conn =
                        DriverManager.getConnection(
                                URL,
                                DB_USER,
                                DB_PASSWORD
                        );

                PreparedStatement pstmt = conn.prepareStatement(query);

                ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                FileInfo file =
                        new FileInfo(
                                rs.getInt("file_id"),
                                rs.getString("file_name"),
                                rs.getString("file_path"),
                                rs.getString("file_type"),
                                rs.getLong("file_size"),
                                rs.getString("status"),
                                rs.getString("upload_date"),
                                rs.getInt("owner_id")
                        );

                files.add(file);
            }

        } catch (SQLException e) {

            System.out.println("Database error while retrieving files.");

            e.printStackTrace();
        }

        return files;
    }


    // DISPLAY ALL FILES


    public void viewAllFiles() {

        List<FileInfo> files = getAllFiles();


        System.out.println("\n==============================================");

        System.out.println("                ALL FILES");

        System.out.println("==============================================");


        if (files.isEmpty()) {

            System.out.println("No files found.");

            System.out.println("==============================================");

            return;
        }


        for (FileInfo file : files) {

            System.out.println(file);
        }


        System.out.println("==============================================");
    }
    // DOWNLOAD FILE portion

    public boolean downloadFile(
            int fileId,
            String destinationPath) {

        String query =
                "SELECT file_name, file_path, status " +
                        "FROM Files " +
                        "WHERE file_id = ?";


        try (
                Connection conn =
                        DriverManager.getConnection(
                                URL,
                                DB_USER,
                                DB_PASSWORD
                        );

                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {

            pstmt.setInt(1, fileId);


            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("File not found.");
                    return false;
                }


                String fileName = rs.getString("file_name");

                String filePath = rs.getString("file_path");

                String status = rs.getString("status");


                if (!"active".equalsIgnoreCase(status)) {

                    System.out.println("File is not active.");

                    return false;
                }


                Path sourcePath = Paths.get(filePath);


                if (!Files.exists(sourcePath)) {

                    System.out.println("Physical file does not exist on server.");

                    return false;
                }


                Path destination = Paths.get(destinationPath);


                // If destinationPath is a directory,
                // keep the original file name.
                if (Files.isDirectory(destination)) {

                    destination = destination.resolve(fileName);
                }


                Path parentDirectory = destination.getParent();


                if (parentDirectory != null && !Files.exists(parentDirectory)) {

                    Files.createDirectories( parentDirectory );
                }


                Files.copy(
                        sourcePath,
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                );


                System.out.println("File downloaded successfully.");

                System.out.println("File ID: " + fileId );

                System.out.println("File Name: " + fileName);

                System.out.println( "Downloaded To: " + destination );


                return true;
            }

        } catch (SQLException e) {

            System.out.println("Database error while downloading file.");

            e.printStackTrace();

            return false;

        } catch (IOException e) {

            System.out.println("Error while copying file.");

            e.printStackTrace();

            return false;
        }
    }
    public boolean deleteFile(int fileId) {

        String selectQuery =
                "SELECT file_path, status " +
                        "FROM Files " +
                        "WHERE file_id = ?";

        String updateQuery =
                "UPDATE Files " +
                        "SET status = 'deleted' " +
                        "WHERE file_id = ? " +
                        "AND status = 'active'";


        try (
                Connection conn =
                        DriverManager.getConnection(
                                URL,
                                DB_USER,
                                DB_PASSWORD
                        );

                PreparedStatement selectStmt =
                        conn.prepareStatement(selectQuery)
        ) {

            // STEP 1: FIND FILE

            selectStmt.setInt(1, fileId);

            String filePath;
            String status;

            try (
                    ResultSet rs =
                            selectStmt.executeQuery()
            ) {

                if (!rs.next()) {

                    System.out.println(
                            "File not found."
                    );

                    return false;
                }

                filePath =
                        rs.getString("file_path");

                status =
                        rs.getString("status");
            }


            // STEP 2: CHECK STATUS

            if (!"active".equalsIgnoreCase(status)) {

                System.out.println(
                        "File is already deleted or inactive."
                );

                return false;
            }


            // STEP 3: DELETE PHYSICAL FILE

            Path physicalFile =
                    Paths.get(filePath);

            if (Files.exists(physicalFile)) {

                Files.delete(physicalFile);

                System.out.println(
                        "Physical file deleted."
                );

            } else {

                System.out.println(
                        "Physical file was not found."
                );
            }


            // STEP 4: UPDATE DATABASE STATUS

            try (
                    PreparedStatement updateStmt =
                            conn.prepareStatement(
                                    updateQuery
                            )
            ) {

                updateStmt.setInt(1, fileId);

                int rowsUpdated =
                        updateStmt.executeUpdate();

                if (rowsUpdated == 1) {

                    System.out.println(
                            "File status changed to DELETED."
                    );

                    return true;
                }

                System.out.println(
                        "Could not update file status."
                );

                return false;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database error while deleting file."
            );

            e.printStackTrace();

            return false;

        } catch (IOException e) {

            System.out.println(
                    "Error while deleting physical file."
            );

            e.printStackTrace();

            return false;
        }
    }


// MODIFY FILE

    public boolean modifyFile(
            int fileId,
            String newFilePath,
            int userId) {

        String selectQuery =
                "SELECT file_path, file_name, owner_id, status " +
                        "FROM Files " +
                        "WHERE file_id = ?";

        String updateQuery =
                "UPDATE Files " +
                        "SET file_size = ? " +
                        "WHERE file_id = ? " +
                        "AND status = 'active'";

        try (
                Connection conn =
                        DriverManager.getConnection(
                                URL,
                                DB_USER,
                                DB_PASSWORD
                        );

                PreparedStatement selectStmt =
                        conn.prepareStatement(selectQuery)
        ) {

            // Find existing file
            selectStmt.setInt(1, fileId);

            String existingFilePath;
            String fileName;
            int ownerId;
            String status;

            try (ResultSet rs = selectStmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("File not found.");
                    return false;
                }

                existingFilePath = rs.getString("file_path");
                fileName = rs.getString("file_name");
                ownerId = rs.getInt("owner_id");
                status = rs.getString("status");
            }

            // File must be active
            if (!"active".equalsIgnoreCase(status)) {
                System.out.println(
                        "Cannot modify an inactive file."
                );
                return false;
            }

            // Only owner can modify for now
            if (ownerId != userId) {
                System.out.println(
                        "You are not the owner of this file."
                );
                return false;
            }

            // Check new file
            Path newFile = Paths.get(newFilePath);

            if (!Files.exists(newFile)) {
                System.out.println(
                        "New file does not exist."
                );
                return false;
            }

            // Replace existing physical file
            Path existingFile =
                    Paths.get(existingFilePath);

            Files.copy(
                    newFile,
                    existingFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Get new size
            long newFileSize =
                    Files.size(existingFile);

            // Update database
            try (
                    PreparedStatement updateStmt =
                            conn.prepareStatement(updateQuery)
            ) {

                updateStmt.setLong(1, newFileSize);
                updateStmt.setInt(2, fileId);

                int rowsUpdated =
                        updateStmt.executeUpdate();

                if (rowsUpdated == 1) {

                    System.out.println(
                            "File modified successfully."
                    );

                    System.out.println(
                            "File ID: " + fileId
                    );

                    System.out.println(
                            "File Name: " + fileName
                    );

                    System.out.println(
                            "New File Size: "
                                    + newFileSize
                                    + " bytes"
                    );

                    return true;
                }

                System.out.println(
                        "Could not update file metadata."
                );

                return false;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database error while modifying file."
            );

            e.printStackTrace();

            return false;

        } catch (IOException e) {

            System.out.println(
                    "Error while modifying physical file."
            );

            e.printStackTrace();

            return false;
        }
    }
}
