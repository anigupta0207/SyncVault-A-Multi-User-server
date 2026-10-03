package client;

import Admin.AdminUserDataService.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UserClientService {

    private static final String SERVER_HOST =
            "100.93.142.78";

    private static final int SERVER_PORT =
            5050;

    public List<UserInfo> getAllUsers()
            throws IOException {

        List<UserInfo> users =
                new ArrayList<>();

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            SERVER_HOST,
                            SERVER_PORT
                    ),
                    5000
            );

            socket.setSoTimeout(7000);

            try (
                    PrintWriter output =
                            new PrintWriter(
                                    socket.getOutputStream(),
                                    true,
                                    StandardCharsets.UTF_8
                            );

                    BufferedReader input =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            )
            ) {

                output.println("GET_USERS");

                String response;

                while ((response =
                        input.readLine()) != null) {

                    if (response.equals("USERS_END")) {
                        break;
                    }

                    if (!response.startsWith("USER\t")) {
                        continue;
                    }

                    String[] fields =
                            response.split("\t", -1);

                    if (fields.length != 6) {
                        continue;
                    }

                    int userId =
                            Integer.parseInt(fields[1]);

                    String name =
                            decode(fields[2]);

                    String email =
                            decode(fields[3]);

                    String role =
                            decode(fields[4]);

                    String status =
                            decode(fields[5]);

                    users.add(
                            new UserInfo(
                                    userId,
                                    name,
                                    email,
                                    role,
                                    status
                            )
                    );
                }
            }
        }

        return users;
    }

    private static String decode(String value) {

        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }
    private static String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8)
                );
    }
    public boolean createUser(
            String name,
            String email,
            String password,
            int roleId
    ) throws IOException {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            SERVER_HOST,
                            SERVER_PORT
                    ),
                    5000
            );

            socket.setSoTimeout(7000);

            try (
                    PrintWriter output =
                            new PrintWriter(
                                    socket.getOutputStream(),
                                    true,
                                    StandardCharsets.UTF_8
                            );

                    BufferedReader input =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            )
            ) {

                String request =
                        "CREATE_USER" + "\t" +
                                encode(name) + "\t" +
                                encode(email) + "\t" +
                                encode(password) + "\t" +
                                roleId;

                output.println(request);

                String response = input.readLine();

                return "CREATE_USER_OK".equals(response);
            }
        }
    }
    public boolean updateUserStatus(
            int userId,
            String status
    ) throws IOException {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            SERVER_HOST,
                            SERVER_PORT
                    ),
                    5000
            );

            socket.setSoTimeout(7000);

            try (
                    PrintWriter output =
                            new PrintWriter(
                                    socket.getOutputStream(),
                                    true,
                                    StandardCharsets.UTF_8
                            );

                    BufferedReader input =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            )
            ) {

                String request =
                        "UPDATE_USER_STATUS" + "\t" +
                                userId + "\t" +
                                encode(status);

                output.println(request);

                String response =
                        input.readLine();

                return "UPDATE_USER_STATUS_OK"
                        .equals(response);
            }
        }
    }
    public boolean updateUserRole(
            int userId,
            int roleId
    ) throws IOException {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            SERVER_HOST,
                            SERVER_PORT
                    ),
                    5000
            );

            socket.setSoTimeout(7000);

            try (
                    PrintWriter output =
                            new PrintWriter(
                                    socket.getOutputStream(),
                                    true,
                                    StandardCharsets.UTF_8
                            );

                    BufferedReader input =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            )
            ) {

                String request =
                        "UPDATE_USER_ROLE"
                                + "\t"
                                + userId
                                + "\t"
                                + roleId;

                output.println(request);

                String response =
                        input.readLine();

                return "UPDATE_USER_ROLE_OK"
                        .equals(response);
            }
        }
    }
}