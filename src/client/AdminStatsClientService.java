package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class AdminStatsClientService {

    private static final String SERVER_HOST = "100.93.142.78";
    private static final int SERVER_PORT = 5050;

    public int[] getStats() throws IOException {

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

                output.println("GET_STATS");

                String response = input.readLine();

                if (response == null) {
                    throw new IOException(
                            "Server closed the connection."
                    );
                }

                if (response.equals("STATS_ERROR")) {
                    throw new IOException(
                            "Server could not retrieve dashboard statistics."
                    );
                }

                String[] fields =
                        response.split("\t", -1);

                if (fields.length != 4 ||
                        !fields[0].equals("STATS")) {

                    throw new IOException(
                            "Invalid statistics response from server."
                    );
                }

                int totalFiles =
                        Integer.parseInt(fields[1]);

                int activeUsers =
                        Integer.parseInt(fields[2]);

                int pendingRequests =
                        Integer.parseInt(fields[3]);

                return new int[]{
                        totalFiles,
                        activeUsers,
                        pendingRequests
                };
            }
        }
    }
}