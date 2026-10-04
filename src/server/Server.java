package server;
import Admin.AdminUserDataService;
import Admin.AdminLoginService;
import auth.AuthenticatedUser;
import file.FileInfo;
import file.FileService;
import Admin.AdminStatsService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import request.Request;
import request.RequestService;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.OutputStream;
import file.FileShareService;
import file.FileShare;
import request.SubmissionService;
public class Server {

    private static final int PORT = 5050;

    private static final String TAB =
            String.valueOf('\t');


    public static void main(String[] args) {

        try (ServerSocket serverSocket =
                     new ServerSocket(PORT)) {

            System.out.println("=================================");
            System.out.println("     SyncVault Server Started");
            System.out.println("=================================");
            System.out.println("Server IP: 100.93.142.78");
            System.out.println("Port: " + PORT);
            System.out.println("Waiting for clients...");


            while (true) {

                Socket clientSocket =
                        serverSocket.accept();

                System.out.println(
                        "Client connected: "
                                + clientSocket.getInetAddress()
                );


                new Thread(
                        () -> handleClient(clientSocket),
                        "syncvault-client"
                ).start();
            }


        } catch (IOException e) {

            System.err.println(
                    "Could not start SyncVault server: "
                            + e.getMessage()
            );
        }
    }
// =====================================================
// HANDLE GET USERS
// =====================================================
private static void handleUpdateUserRole(
        String message,
        PrintWriter output
) {

    try {

        String[] parts =
                message.split(TAB, -1);

        if (parts.length != 3) {

            output.println(
                    "UPDATE_USER_ROLE_ERROR\tInvalid request"
            );

            return;
        }

        int userId =
                Integer.parseInt(parts[1]);

        int roleId =
                Integer.parseInt(parts[2]);

        if (roleId < 1 || roleId > 3) {

            output.println(
                    "UPDATE_USER_ROLE_ERROR\tInvalid role"
            );

            return;
        }

        AdminUserDataService userService =
                new AdminUserDataService();

        boolean updated =
                userService.updateUserRole(
                        userId,
                        roleId
                );

        if (updated) {

            output.println(
                    "UPDATE_USER_ROLE_OK"
            );

        } else {

            output.println(
                    "UPDATE_USER_ROLE_ERROR\tUser not found"
            );
        }

    } catch (NumberFormatException e) {

        output.println(
                "UPDATE_USER_ROLE_ERROR\tInvalid user ID or role ID"
        );

    } catch (SQLException e) {

        System.err.println(
                "Error while updating user role: "
                        + e.getMessage()
        );

        output.println(
                "UPDATE_USER_ROLE_ERROR\tDatabase error"
        );

    } catch (Exception e) {

        System.err.println(
                "Unexpected error while updating user role: "
                        + e.getMessage()
        );

        output.println(
                "UPDATE_USER_ROLE_ERROR\tUnexpected server error"
        );
    }
}
    private static void handleGetUsers(PrintWriter output) {

        try {

            AdminUserDataService userService =
                    new AdminUserDataService();

            List<AdminUserDataService.UserInfo> users =
                    userService.getAllUsers();

            for (
                    AdminUserDataService.UserInfo user :
                    users
            ) {

                output.println(
                        "USER" + TAB +
                                user.getUserId() + TAB +
                                encode(user.getName()) + TAB +
                                encode(user.getEmail()) + TAB +
                                encode(user.getRole()) + TAB +
                                encode(user.getStatus())
                );
            }

            output.println("USERS_END");

        } catch (SQLException e) {

            System.err.println(
                    "Error while retrieving users: "
                            + e.getMessage()
            );

            output.println("USERS_END");
        }
    }
    private static void handleCreateUser(
            String message,
            PrintWriter output
    ) {

        try {

            String[] parts = message.split(TAB, -1);

            if (parts.length != 5) {
                output.println("CREATE_USER_ERROR\tInvalid request");
                return;
            }

            String name = decode(parts[1]);
            String email = decode(parts[2]);
            String password = decode(parts[3]);
            int roleId = Integer.parseInt(parts[4]);

            AdminUserDataService userService =
                    new AdminUserDataService();

            int result =
                    userService.createUser(
                            name,
                            email,
                            password,
                            roleId
                    );

            if (result > 0) {
                output.println("CREATE_USER_OK");
            } else {
                output.println(
                        "CREATE_USER_ERROR\tUser was not created"
                );
            }

        } catch (NumberFormatException e) {

            output.println(
                    "CREATE_USER_ERROR\tInvalid role ID"
            );

        } catch (SQLException e) {

            System.err.println(
                    "Error while creating user: "
                            + e.getMessage()
            );

            output.println(
                    "CREATE_USER_ERROR\tDatabase error"
            );

        } catch (Exception e) {

            System.err.println(
                    "Unexpected error while creating user: "
                            + e.getMessage()
            );

            output.println(
                    "CREATE_USER_ERROR\tUnexpected server error"
            );
        }
    }
    private static void handleDownloadFile(
            String message,
            PrintWriter output) {

        try {

            String[] parts =
                    message.split(TAB, -1);

            if (parts.length != 2) {

                output.println(
                        "DOWNLOAD_ERROR\tInvalid request"
                );

                return;
            }

            int fileId =
                    Integer.parseInt(parts[1]);

            FileService fileService =
                    new FileService();

            /*
             * We need a method that returns the
             * physical file information.
             */
            FileInfo file =
                    fileService.getFileById(fileId);

            if (file == null) {

                output.println(
                        "DOWNLOAD_ERROR\tFile not found"
                );

                return;
            }

            Path filePath =
                    Paths.get(file.getFilePath());

            if (!Files.exists(filePath)) {

                output.println(
                        "DOWNLOAD_ERROR\tPhysical file not found"
                );

                return;
            }

            long fileSize =
                    Files.size(filePath);

            output.println(
                    "DOWNLOAD_OK" + TAB +
                            encode(file.getFileName()) + TAB +
                            fileSize
            );

            try (InputStream fileInput =
                         Files.newInputStream(filePath)) {

                byte[] buffer =
                        new byte[8192];

                int bytesRead;

                while ((bytesRead =
                        fileInput.read(buffer)) != -1) {

                    String encoded =
                            Base64.getEncoder()
                                    .encodeToString(
                                            java.util.Arrays.copyOf(
                                                    buffer,
                                                    bytesRead
                                            )
                                    );

                    output.println(
                            "DATA" + TAB + encoded
                    );
                }
            }

            output.println("DOWNLOAD_END");

        } catch (NumberFormatException e) {

            output.println(
                    "DOWNLOAD_ERROR\tInvalid file ID"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error during file download: "
                            + e.getMessage()
            );

            output.println(
                    "DOWNLOAD_ERROR\tServer error"
            );
        }
    }
    private static void handleUploadFile(
            String firstMessage,
            BufferedReader input,
            PrintWriter output) {

        Path tempFile = null;

        try {

            String[] parts =
                    firstMessage.split(TAB, -1);

            if (parts.length != 4) {

                output.println(
                        "UPLOAD_ERROR\tInvalid request"
                );

                return;
            }

            int ownerId =
                    Integer.parseInt(parts[1]);

            String fileName =
                    decode(parts[2]);

            long expectedSize =
                    Long.parseLong(parts[3]);

            if (expectedSize < 0) {

                output.println(
                        "UPLOAD_ERROR\tInvalid file size"
                );

                return;
            }

            /*
             * Create temporary server-side file.
             */
            Path tempDirectory =
                    java.nio.file.Paths.get(
                            "storage",
                            "temp"
                    );

            java.nio.file.Files.createDirectories(
                    tempDirectory
            );

            tempFile =
                    java.nio.file.Files.createTempFile(
                            tempDirectory,
                            "upload_",
                            "_" + fileName
                    );

            long receivedBytes = 0;

            try (FileOutputStream fileOutput =
                         new FileOutputStream(
                                 tempFile.toFile()
                         )) {

                while (true) {

                    String message =
                            input.readLine();

                    if (message == null) {

                        throw new IOException(
                                "Client disconnected during upload"
                        );
                    }

                    if (message.equals("UPLOAD_END")) {
                        break;
                    }

                    if (!message.startsWith("DATA" + TAB)) {

                        throw new IOException(
                                "Invalid upload data"
                        );
                    }

                    String encodedData =
                            message.substring(5);

                    byte[] chunk =
                            Base64.getDecoder()
                                    .decode(encodedData);

                    fileOutput.write(chunk);

                    receivedBytes += chunk.length;

                    if (receivedBytes > expectedSize) {

                        throw new IOException(
                                "Received more data than expected"
                        );
                    }
                }
            }

            /*
             * Verify file size.
             */
            if (receivedBytes != expectedSize) {

                output.println(
                        "UPLOAD_ERROR\tFile size mismatch"
                );

                return;
            }

            /*
             * Pass temporary file to existing FileService.
             */
            FileService fileService =
                    new FileService();

            boolean uploaded =
                    fileService.uploadFile(
                            tempFile.toString(),
                            fileName,
                            ownerId
                    );

            if (uploaded) {

                output.println("UPLOAD_OK");

                System.out.println(
                        "File uploaded from client: "
                                + fileName
                );

            } else {

                output.println(
                        "UPLOAD_ERROR\tFileService failed"
                );
            }

        } catch (NumberFormatException e) {

            output.println(
                    "UPLOAD_ERROR\tInvalid number"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error during file upload: "
                            + e.getMessage()
            );

            output.println(
                    "UPLOAD_ERROR\t"
                            + e.getMessage()
            );

        } finally {

            /*
             * Remove temporary file.
             */
            if (tempFile != null) {

                try {

                    java.nio.file.Files.deleteIfExists(tempFile);

                } catch (IOException e) {

                    System.err.println(
                            "Could not delete temporary upload file: "
                                    + e.getMessage()
                    );
                }
            }
        }
    }
    // =====================================================
    // HANDLE CLIENT
    // =====================================================
    private static void handleClient(Socket clientSocket) {

        try (
                Socket socket = clientSocket;

                BufferedReader input =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        );

                PrintWriter output =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true,
                                StandardCharsets.UTF_8
                        )
        ) {

            String firstMessage = input.readLine();

            if (firstMessage == null) {
                return;
            }

            System.out.println(
                    "Received: " + firstMessage
            );


            // ==========================================
            // AUTHENTICATION
            // ==========================================

            if (firstMessage.startsWith("AUTH" + TAB)) {

                handleAuthentication(
                        firstMessage,
                        output
                );

                return;
            }

            // ==========================================
            // UPLOAD FILE
            // ==========================================

            if (firstMessage.startsWith("UPLOAD_FILE" + TAB)) {

                handleUploadFile(
                        firstMessage,
                        input,
                        output
                );

                return;
            }
            // ==========================================
            // DOWNLOAD FILE
            // ==========================================

            if (firstMessage.startsWith("DOWNLOAD_FILE" + TAB)) {

                handleDownloadFile(
                        firstMessage,
                        output
                );

                return;
            }
            // ==========================================
            // DELETE FILE
            // ==========================================

            if (firstMessage.startsWith("DELETE_FILE" + TAB)) {

                handleDeleteFile(
                        firstMessage,
                        output
                );

                return;
            }
            if (firstMessage.startsWith("MODIFY_FILE" + TAB)) {
                handleModifyFile(firstMessage, input, output);
                return;
            }
            // ==========================================
            // GET FILES
            // ==========================================

            if (firstMessage.equals("GET_FILES")) {

                handleGetFiles(output);

                return;
            }


            // ==========================================
            // GET DASHBOARD STATISTICS
            // ==========================================

            if (firstMessage.equals("GET_STATS")) {

                handleGetStats(output);

                return;
            }
            // ==========================================
            // GET REQUESTS
            // ==========================================

                        if (firstMessage.equals("GET_REQUESTS")) {

                            handleGetRequests(output);

                            return;
                        }

            // ==========================================
            // CREATE REQUEST
            // ==========================================

            if (firstMessage.startsWith("CREATE_REQUEST" + TAB)) {

                handleCreateRequest(
                        firstMessage,
                        output
                );

                return;
            }
            // ==========================================
            // GET USERS
            // ==========================================
            if (firstMessage.startsWith("SUBMIT_FILE" + TAB)) {
                handleSubmitFile(firstMessage, output);
                return;
            }
            if (firstMessage.startsWith("APPROVE_SUBMISSION" + TAB)) {
                handleApproveSubmission(firstMessage, output);
                return;
            }

            if (firstMessage.startsWith("REJECT_SUBMISSION" + TAB)) {
                handleRejectSubmission(firstMessage, output);
                return;
            }
            if (firstMessage.equals("GET_USERS")) {

                handleGetUsers(output);

                return;
            }
            if (firstMessage.startsWith("GET_SHARED_FILES" + TAB)) {
                handleGetSharedFiles(firstMessage, output);
                return;
            }
            if (firstMessage.startsWith("SHARE_FILE" + TAB)) {
                handleShareFile(firstMessage, output);
                return;
            }
            if (firstMessage.startsWith("UPDATE_USER_STATUS" + TAB)) {
                handleUpdateUserStatus(firstMessage, output);
                return;
            }

            if (firstMessage.startsWith("CREATE_USER" + TAB)) {
                handleCreateUser(firstMessage, output);
                return;
            }
            if (firstMessage.startsWith("UPDATE_USER_ROLE" + TAB)) {
                handleUpdateUserRole(firstMessage, output);
                return;
            }

            // ==========================================
            // NORMAL TWO-WAY CHAT
            // ==========================================

            System.out.println(
                    "CLIENT: " + firstMessage
            );

            output.println(
                    "Server received: " + firstMessage
            );


            // ------------------------------------------
            // THREAD: CLIENT → SERVER
            // ------------------------------------------

            Thread receiverThread =
                    new Thread(() -> {

                        try {

                            String message;

                            while ((message = input.readLine()) != null) {

                                System.out.println(
                                        "\nCLIENT: " + message
                                );

                                System.out.print(
                                        "SERVER: "
                                );
                            }

                        } catch (IOException e) {

                            System.out.println(
                                    "\nClient disconnected."
                            );
                        }

                    }, "syncvault-client-receiver");


            receiverThread.start();


            // ------------------------------------------
            // MAIN THREAD: SERVER → CLIENT
            // ------------------------------------------

            BufferedReader keyboard =
                    new BufferedReader(
                            new InputStreamReader(
                                    System.in,
                                    StandardCharsets.UTF_8
                            )
                    );


            while (true) {

                System.out.print(
                        "SERVER: "
                );

                String message =
                        keyboard.readLine();


                if (message == null) {
                    break;
                }


                output.println(message);


                if (message.equalsIgnoreCase("exit")) {

                    break;
                }
            }


        } catch (IOException e) {

            System.out.println(
                    "Client disconnected: "
                            + e.getMessage()
            );
        }
    }
    private static void handleDeleteFile(
            String message,
            PrintWriter output) {

        try {

            String[] parts =
                    message.split(TAB, -1);

            if (parts.length != 2) {

                output.println(
                        "DELETE_ERROR\tInvalid request"
                );

                return;
            }

            int fileId =
                    Integer.parseInt(parts[1]);

            FileService fileService =
                    new FileService();

            boolean deleted =
                    fileService.deleteFile(fileId);

            if (deleted) {

                output.println("DELETE_OK");

                System.out.println(
                        "File deleted successfully. File ID: "
                                + fileId
                );

            } else {

                output.println(
                        "DELETE_ERROR\tFile could not be deleted"
                );
            }

        } catch (NumberFormatException e) {

            output.println(
                    "DELETE_ERROR\tInvalid file ID"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error while deleting file: "
                            + e.getMessage()
            );

            output.println(
                    "DELETE_ERROR\tServer error"
            );
        }
    }
    private static void handleCreateRequest(
            String message,
            PrintWriter output) {

        try {

            String[] parts =
                    message.split(TAB, -1);

            if (parts.length != 5) {

                output.println(
                        "CREATE_REQUEST_ERROR\tInvalid request"
                );

                return;
            }

            int userId =
                    Integer.parseInt(parts[1]);

            Integer fileId = null;

            if (!parts[2].isEmpty()) {

                fileId =
                        Integer.parseInt(parts[2]);
            }

            String requestType =
                    decode(parts[3]);

            int priority =
                    Integer.parseInt(parts[4]);

            if (priority < 1 || priority > 5) {

                output.println(
                        "CREATE_REQUEST_ERROR\tInvalid priority"
                );

                return;
            }

            RequestService requestService =
                    new RequestService();

            boolean created =
                    requestService.createRequest(
                            userId,
                            fileId,
                            requestType,
                            priority
                    );

            if (created) {

                output.println(
                        "CREATE_REQUEST_OK"
                );

            } else {

                output.println(
                        "CREATE_REQUEST_ERROR\tCould not create request"
                );
            }

        } catch (NumberFormatException e) {

            output.println(
                    "CREATE_REQUEST_ERROR\tInvalid number"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error while creating request: "
                            + e.getMessage()
            );

            output.println(
                    "CREATE_REQUEST_ERROR\tDatabase error"
            );
        }
    }

    // =====================================================
    // AUTHENTICATION
    // =====================================================
    private static void handleGetStats(PrintWriter output) {

        try {

            AdminStatsService statsService =
                    new AdminStatsService();

            int totalFiles =
                    statsService.getTotalFiles();

            int activeUsers =
                    statsService.getActiveUsers();

            int pendingRequests =
                    statsService.getPendingRequests();

            output.println(
                    "STATS\t" +
                            totalFiles + "\t" +
                            activeUsers + "\t" +
                            pendingRequests
            );

        } catch (SQLException e) {

            System.err.println(
                    "Error while retrieving dashboard statistics: "
                            + e.getMessage()
            );

            output.println("STATS_ERROR");
        }
    }
    private static void handleGetRequests(
            PrintWriter output) {

        try {

            RequestService requestService =
                    new RequestService();

            List<Request> requests =
                    requestService.getPendingRequests();

            for (Request request : requests) {

                output.println(
                        "REQUEST" + TAB +
                                request.getRequestId() + TAB +
                                request.getUserId() + TAB +
                                (request.getFileId() == null
                                        ? ""
                                        : request.getFileId()) + TAB +
                                encode(request.getRequestType()) + TAB +
                                encode(request.getStatus()) + TAB +
                                request.getPriority() + TAB +
                                encode(request.getRequestTime())
                );
            }

            output.println("REQUESTS_END");

        } catch (Exception e) {

            System.err.println(
                    "Error while retrieving requests: "
                            + e.getMessage()
            );

            output.println("REQUESTS_ERROR");
        }
    }
    private static void handleAuthentication(
            String message,
            PrintWriter output) {

        try {

            String[] fields =
                    message.split(TAB, -1);


            if (fields.length != 3) {

                output.println("AUTH_FAIL");

                return;
            }


            String email =
                    decode(fields[1]);

            String password =
                    decode(fields[2]);


            AuthenticatedUser user =
                    new AdminLoginService()
                            .authenticateUser(
                                    email,
                                    password
                            );


            if (user == null) {

                output.println("AUTH_FAIL");

                return;
            }


            String sessionToken =
                    SessionManager.createSession(user);

            output.println(
                    String.join(
                            TAB,
                            "AUTH_OK",
                            String.valueOf(user.getUserId()),
                            encode(user.getName()),
                            encode(user.getEmail()),
                            encode(user.getRole()),
                            encode(sessionToken)
                    )
            );


        } catch (SQLException e) {

            System.err.println(
                    "The SyncVault server could not "
                            + "authenticate against its database."
            );

            output.println("DB_ERROR");


        } catch (IllegalArgumentException e) {

            output.println("AUTH_FAIL");
        }
    }


    // =====================================================
    // GET FILES FROM DATABASE
    // =====================================================

    private static void handleGetFiles(
            PrintWriter output) {

        try {

            FileService fileService =
                    new FileService();


            List<FileInfo> files =
                    fileService.getAllFiles();


            for (FileInfo file : files) {

                output.println(
                        "FILE" + TAB +

                                file.getFileId() + TAB +


                                encode(
                                        file.getFileName()
                                ) + TAB +

                                encode(
                                        file.getFilePath()
                                ) + TAB +

                                encode(
                                        file.getFileType()
                                ) + TAB +

                                file.getFileSize() + TAB +

                                encode(
                                        file.getStatus()
                                ) + TAB +

                                file.getOwnerId()
                );
            }


            // Tell client that all records
            // have been sent.

            output.println("FILES_END");


        } catch (Exception e) {

            System.err.println(
                    "Error while retrieving files: "
                            + e.getMessage()
            );


            // Still send the ending signal
            // so client does not wait forever.

            output.println("FILES_END");
        }
    }


    // =====================================================
    // BASE64 ENCODE
    // =====================================================

    private static String encode(
            String value) {

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(
                                StandardCharsets.UTF_8
                        )
                );
    }


    // =====================================================
    // BASE64 DECODE
    // =====================================================

    private static String decode(
            String value) {

        return new String(
                Base64
                        .getUrlDecoder()
                        .decode(value),
                StandardCharsets.UTF_8
        );
    }
    private static void handleUpdateUserStatus(
            String message,
            PrintWriter output
    ) {

        try {

            String[] parts =
                    message.split(TAB, -1);

            if (parts.length != 3) {

                output.println(
                        "UPDATE_USER_STATUS_ERROR\tInvalid request"
                );

                return;
            }


            int userId =
                    Integer.parseInt(parts[1]);

            String status =
                    decode(parts[2]);


            if (
                    !status.equals("active")
                            &&
                            !status.equals("inactive")
            ) {

                output.println(
                        "UPDATE_USER_STATUS_ERROR\tInvalid status"
                );

                return;
            }


            AdminUserDataService userService =
                    new AdminUserDataService();


            boolean updated =
                    userService.updateUserStatus(
                            userId,
                            status
                    );


            if (updated) {

                output.println(
                        "UPDATE_USER_STATUS_OK"
                );

            } else {

                output.println(
                        "UPDATE_USER_STATUS_ERROR\tUser not found"
                );
            }


        } catch (NumberFormatException e) {

            output.println(
                    "UPDATE_USER_STATUS_ERROR\tInvalid user ID"
            );


        } catch (SQLException e) {

            System.err.println(
                    "Error while updating user status: "
                            + e.getMessage()
            );

            output.println(
                    "UPDATE_USER_STATUS_ERROR\tDatabase error"
            );


        } catch (Exception e) {

            System.err.println(
                    "Unexpected error while updating user status: "
                            + e.getMessage()
            );

            output.println(
                    "UPDATE_USER_STATUS_ERROR\tUnexpected server error"
            );
        }
    }
    //  modify File

    private static void handleModifyFile(
            String firstMessage,
            BufferedReader input,
            PrintWriter output) {

        Path tempFile = null;

        try {

            String[] parts = firstMessage.split("\t");

            if (parts.length != 4) {
                output.println("MODIFY_ERROR\tInvalid request");
                return;
            }

            int fileId = Integer.parseInt(parts[1]);
            int userId = Integer.parseInt(parts[2]);
            long expectedSize = Long.parseLong(parts[3]);

            Path tempDir = Paths.get("storage", "temp");
            Files.createDirectories(tempDir);

            tempFile = Files.createTempFile(
                    tempDir,
                    "modify_",
                    ".tmp"
            );

            long receivedSize = 0;

            try (OutputStream fileOutput =
                         Files.newOutputStream(tempFile)) {

                String line;

                while (true) {

                    line = input.readLine();

                    if (line == null) {
                        break;
                    }

                    if ("MODIFY_END".equals(line)) {
                        break;
                    }

                    if (!line.startsWith("DATA\t")) {
                        output.println(
                                "MODIFY_ERROR\tInvalid data"
                        );
                        return;
                    }

                    String encodedData = line.substring(5);

                    byte[] chunk =
                            Base64.getDecoder().decode(encodedData);

                    fileOutput.write(chunk);
                    receivedSize += chunk.length;
                }
            }

            if (receivedSize != expectedSize) {

                output.println(
                        "MODIFY_ERROR\tSize mismatch"
                );

                return;
            }

            // FileService.modifyFile() is an instance method
            FileService fileService = new FileService();

            boolean success = fileService.modifyFile(
                    fileId,
                    tempFile.toString(),
                    userId
            );

            if (success) {
                output.println("MODIFY_OK");
            } else {
                output.println(
                        "MODIFY_ERROR\tModification rejected"
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Error modifying file: " + e.getMessage()
            );

            output.println(
                    "MODIFY_ERROR\t" + e.getMessage()
            );

        } finally {

            if (tempFile != null) {

                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    System.err.println(
                            "Could not delete temporary file: "
                                    + e.getMessage()
                    );
                }
            }
        }
    }
    private static void handleShareFile(
            String message,
            PrintWriter output) {

        try {

            String[] parts =
                    message.split(TAB, -1);

            if (parts.length != 5) {
                output.println(
                        "SHARE_ERROR\tInvalid request"
                );
                return;
            }

            int fileId =
                    Integer.parseInt(parts[1]);

            int sharedBy =
                    Integer.parseInt(parts[2]);

            int sharedWith =
                    Integer.parseInt(parts[3]);

            String permission =
                    parts[4];

            FileShareService service =
                    new FileShareService();

            boolean success =
                    service.shareFile(
                            fileId,
                            sharedBy,
                            sharedWith,
                            permission
                    );

            if (success) {
                output.println("SHARE_OK");
            } else {
                output.println(
                        "SHARE_ERROR\tShare rejected"
                );
            }

        } catch (NumberFormatException e) {

            output.println(
                    "SHARE_ERROR\tInvalid ID"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error while sharing file: "
                            + e.getMessage()
            );

            output.println(
                    "SHARE_ERROR\tDatabase error"
            );
        }
    }
    private static void handleGetSharedFiles(
            String message,
            PrintWriter output) {

        try {

            String[] parts =
                    message.split(TAB, -1);

            if (parts.length != 2) {
                output.println(
                        "SHARED_FILES_ERROR\tInvalid request"
                );
                return;
            }

            int userId =
                    Integer.parseInt(parts[1]);

            FileShareService service =
                    new FileShareService();

            List<FileShare> shares =
                    service.getSharedFiles(userId);

            for (FileShare share : shares) {

                output.println(
                        "SHARED_FILE" + TAB +
                                share.getShareId() + TAB +
                                share.getFileId() + TAB +
                                share.getSharedBy() + TAB +
                                share.getSharedWith() + TAB +
                                encode(share.getPermission()) + TAB +
                                encode(share.getSharedAt()) + TAB +
                                encode(share.getFileName()) + TAB +
                                share.getFileSize() + TAB +
                                encode(share.getFileType())
                );
            }
            output.println("SHARED_FILES_END");

        } catch (NumberFormatException e) {

            output.println(
                    "SHARED_FILES_ERROR\tInvalid user ID"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error while retrieving shared files: "
                            + e.getMessage()
            );

            output.println(
                    "SHARED_FILES_ERROR\tDatabase error"
            );
        }
    }
    private static void handleSubmitFile(
            String message,
            PrintWriter output) {

        try {
            String[] parts = message.split(TAB, -1);

            if (parts.length != 4) {
                output.println("SUBMIT_ERROR\tInvalid request");
                return;
            }

            int studentId = Integer.parseInt(parts[1]);
            int fileId = Integer.parseInt(parts[2]);
            int priority = Integer.parseInt(parts[3]);

            SubmissionService service = new SubmissionService();

            boolean success = service.submitFile(
                    studentId,
                    fileId,
                    priority
            );

            if (success) {
                output.println("SUBMIT_OK");
            } else {
                output.println("SUBMIT_ERROR\tSubmission failed");
            }

        } catch (NumberFormatException e) {

            output.println("SUBMIT_ERROR\tInvalid ID or priority");

        } catch (Exception e) {

            System.err.println(
                    "Error while submitting file: "
                            + e.getMessage()
            );

            output.println(
                    "SUBMIT_ERROR\tServer error"
            );
        }
    }
    private static void handleApproveSubmission(
            String message,
            PrintWriter output) {

        try {
            String[] parts = message.split(TAB, -1);

            if (parts.length != 2) {
                output.println("APPROVE_ERROR\tInvalid request");
                return;
            }

            int requestId = Integer.parseInt(parts[1]);

            SubmissionService service =
                    new SubmissionService();

            boolean success =
                    service.approveSubmission(requestId);

            if (success) {
                output.println("APPROVE_OK");
            } else {
                output.println(
                        "APPROVE_ERROR\tApproval failed"
                );
            }

        } catch (NumberFormatException e) {

            output.println(
                    "APPROVE_ERROR\tInvalid request ID"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error while approving submission: "
                            + e.getMessage()
            );

            output.println(
                    "APPROVE_ERROR\tServer error"
            );
        }
    }


    private static void handleRejectSubmission(
            String message,
            PrintWriter output) {

        try {
            String[] parts = message.split(TAB, -1);

            if (parts.length != 2) {
                output.println("REJECT_ERROR\tInvalid request");
                return;
            }

            int requestId = Integer.parseInt(parts[1]);

            SubmissionService service =
                    new SubmissionService();

            boolean success =
                    service.rejectSubmission(requestId);

            if (success) {
                output.println("REJECT_OK");
            } else {
                output.println(
                        "REJECT_ERROR\tRejection failed"
                );
            }

        } catch (NumberFormatException e) {

            output.println(
                    "REJECT_ERROR\tInvalid request ID"
            );

        } catch (Exception e) {

            System.err.println(
                    "Error while rejecting submission: "
                            + e.getMessage()
            );

            output.println(
                    "REJECT_ERROR\tServer error"
            );
        }
    }

}