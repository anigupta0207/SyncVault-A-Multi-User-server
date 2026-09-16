package server;

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {

        int port = 5050;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("SyncVault Server started...");
            System.out.println("Waiting for client on port " + port);

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println(
                        "Client connected: "
                                + clientSocket.getInetAddress()
                );

                BufferedReader input =
                        new BufferedReader(
                                new InputStreamReader(
                                        clientSocket.getInputStream()
                                )
                        );

                PrintWriter output =
                        new PrintWriter(
                                clientSocket.getOutputStream(),
                                true
                        );

                String message = input.readLine();

                System.out.println("Received: " + message);

                output.println("Hello from Mac Server");

                clientSocket.close();

                System.out.println("Client disconnected.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}