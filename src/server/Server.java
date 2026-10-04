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

            if (firstMessage.equals("GET_USERS")) {

                handleGetUsers(output);

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


            output.println(
                    String.join(
                            TAB,
                            "AUTH_OK",
                            String.valueOf(
                                    user.getUserId()
                            ),
                            encode(
                                    user.getName()
                            ),
                            encode(
                                    user.getEmail()
                            ),
                            encode(
                                    user.getRole()
                            )
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
}