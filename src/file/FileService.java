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

    public boolean uploadFile(
            String sourceFilePath,
            String fileName,
            int ownerId) {

        Path sourcePath =
                Paths.get(sourceFilePath);

        if (!Files.exists(sourcePath)) {

            System.out.println(
                    "Source file does not exist."
            );

            return false;
        }


        // Create owner-specific directory
        Path ownerDirectory =
                Paths.get(
                        STORAGE_DIRECTORY,
                        String.valueOf(ownerId)
                );

        try {

            if (!Files.exists(ownerDirectory)) {

                Files.createDirectories(
                        ownerDirectory
                );
            }


            // Destination of the actual file
            Path destinationPath =
                    ownerDirectory.resolve(fileName);


            // Copy file to server storage
            Files.copy(
                    sourcePath,
                    destinationPath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // Get file information
            long fileSize =
                    Files.size(destinationPath);

            String fileType =
                    getFileType(fileName);

            String databasePath =
                    destinationPath
                            .toString()
                            .replace("\\", "/");


            // Insert metadata into database
            String query =
                    "INSERT INTO Files " +
                            "(file_name, file_path, file_type, " +
                            "file_size, owner_id) " +
                            "VALUES (?, ?, ?, ?, ?)";


            try (
                    Connection conn =
                            DriverManager.getConnection(
                                    URL,
                                    DB_USER,
                                    DB_PASSWORD
                            );

                    PreparedStatement pstmt =
                            conn.prepareStatement(
                                    query,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                pstmt.setString(
                        1,
                        fileName
                );

                pstmt.setString(
                        2,
                        databasePath
                );

                pstmt.setString(
                        3,
                        fileType
                );

                pstmt.setLong(
                        4,
                        fileSize
                );

                pstmt.setInt(
                        5,
                        ownerId
                );


                int rowsInserted =
                        pstmt.executeUpdate();


                if (rowsInserted != 1) {

                    System.out.println(
                            "File metadata could not be stored."
                    );

                    // Remove copied file if DB insert failed
                    Files.deleteIfExists(
                            destinationPath
                    );

                    return false;
                }


                try (
                        ResultSet rs =
                                pstmt.getGeneratedKeys()
                ) {

                    if (rs.next()) {

                        System.out.println(
                                "File uploaded successfully."
                        );

                        System.out.println(
                                "File ID: "
                                        + rs.getInt(1)
                        );

                        System.out.println(
                                "File Name: "
                                        + fileName
                        );

                        System.out.println(
                                "Stored At: "
                                        + databasePath
                        );

                        System.out.println(
                                "File Size: "
                                        + fileSize
                                        + " bytes"
                        );
                    }
                }

                return true;
            }

        } catch (IOException e) {

            System.out.println(
                    "Error while storing file."
            );

            e.printStackTrace();

            return false;

        } catch (SQLException e) {

            System.out.println(
                    "Database error while storing file metadata."
            );

            e.printStackTrace();

            return false;
        }
    }


    // type of file

    private String getFileType(String fileName) {

        int lastDot =
                fileName.lastIndexOf('.');

        if (lastDot == -1 ||
                lastDot == fileName.length() - 1) {

            return "unknown";
        }

        return fileName
                .substring(lastDot + 1)
                .toLowerCase();
    }

    // GET ALL FILES

    public List<FileInfo> getAllFiles() {

        List<FileInfo> files =
                new ArrayList<>();

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

                PreparedStatement pstmt =
                        conn.prepareStatement(query);

                ResultSet rs =
                        pstmt.executeQuery()
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

            System.out.println(
                    "Database error while retrieving files."
            );

            e.printStackTrace();
        }

        return files;
    }


    // DISPLAY ALL FILES


    public void viewAllFiles() {

        List<FileInfo> files =
                getAllFiles();


        System.out.println(
                "\n=============================================="
        );

        System.out.println(
                "                ALL FILES"
        );

        System.out.println(
                "=============================================="
        );


        if (files.isEmpty()) {

            System.out.println(
                    "No files found."
            );

            System.out.println(
                    "=============================================="
            );

            return;
        }


        for (FileInfo file : files) {

            System.out.println(file);
        }


        System.out.println(
                "=============================================="
        );
    }
}