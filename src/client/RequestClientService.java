package client;

import request.Request;

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

public class RequestClientService {

    private static final String SERVER_HOST =
            "100.93.142.78";

    private static final int SERVER_PORT =
            5050;

    public List<Request> getPendingRequests()
            throws IOException {

        List<Request> requests =
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

                // Ask server for pending requests
                output.println("GET_REQUESTS");

                String response;

                while ((response =
                        input.readLine()) != null) {

                    // Server finished sending requests
                    if (response.equals("REQUESTS_END")) {
                        break;
                    }

                    // Server reported an error
                    if (response.equals("REQUESTS_ERROR")) {
                        throw new IOException(
                                "Server could not retrieve requests."
                        );
                    }

                    // Ignore unexpected responses
                    if (!response.startsWith("REQUEST\t")) {
                        continue;
                    }

                    String[] fields =
                            response.split("\t", -1);

                    if (fields.length != 8) {
                        continue;
                    }

                    int requestId =
                            Integer.parseInt(fields[1]);

                    int userId =
                            Integer.parseInt(fields[2]);

                    Integer fileId = null;

                    if (!fields[3].isEmpty()) {
                        fileId =
                                Integer.parseInt(fields[3]);
                    }

                    String requestType =
                            decode(fields[4]);

                    String status =
                            decode(fields[5]);

                    int priority =
                            Integer.parseInt(fields[6]);

                    String requestTime =
                            decode(fields[7]);

                    Request request =
                            new Request(
                                    requestId,
                                    userId,
                                    fileId,
                                    requestType,
                                    status,
                                    priority,
                                    requestTime
                            );

                    requests.add(request);
                }
            }
        }

        return requests;
    }

    private static String decode(String value) {

        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }
}