package Client;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {

        String serverIp = "100.93.142.78";
        int port = 5050;

        try (
                Socket socket = new Socket(serverIp, port);

                BufferedReader input =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                PrintWriter output =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true
                        )
        ) {

            System.out.println("Connected to SyncVault Server.");

            output.println("Hello from Windows");

            String response = input.readLine();

            System.out.println("Server response: " + response);

        } catch (IOException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }
}