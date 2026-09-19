package client;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {

        String serverIp = "100.93.142.78";
        int port = 5050;

        try (
                Socket socket = new Socket(serverIp, port);

                BufferedReader serverInput =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                PrintWriter serverOutput =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true
                        );

                BufferedReader keyboard =
                        new BufferedReader(
                                new InputStreamReader(
                                        System.in
                                )
                        )
        ) {

            System.out.println("Connected to SyncVault Server.");

            // Read server's initial messages
            System.out.println("Server: " + serverInput.readLine());
            System.out.println("Server: " + serverInput.readLine());

            while (true) {

                System.out.print("\nYou: ");

                String message = keyboard.readLine();

                if (message == null) {
                    break;
                }

                serverOutput.println(message);

                String response = serverInput.readLine();

                System.out.println("Server: " + response);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (IOException e) {

            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }
}