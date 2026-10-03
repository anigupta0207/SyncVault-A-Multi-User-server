package client;

import file.FileInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileClientService {

    private static final String SERVER_HOST = "100.93.142.78";
    private static final int SERVER_PORT = 5050;

    public List<FileInfo> getAllFiles() throws IOException {

        List<FileInfo> files = new ArrayList<>();

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(SERVER_HOST, SERVER_PORT),
                    5000
            );

            socket.setSoTimeout(7000);

            try (
                    PrintWriter output = new PrintWriter(
                            socket.getOutputStream(),
                            true,
                            StandardCharsets.UTF_8
                    );

                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    )
            ) {

                // Ask server for files
                output.println("GET_FILES");

                String response;

                while ((response = input.readLine()) != null) {

                    if (response.equals("FILES_END")) {
                        break;
                    }

                    if (!response.startsWith("FILE\t")) {
                        continue;
                    }

                    String[] fields = response.split("\t", -1);

                    if (fields.length != 8) {
                        continue;
                    }

                    int fileId = Integer.parseInt(fields[1]);

                    String fileName = decode(fields[2]);
                    String filePath = decode(fields[3]);
                    String fileType = decode(fields[4]);

                    long fileSize = Long.parseLong(fields[5]);

                    String status = decode(fields[6]);

                    int ownerId = Integer.parseInt(fields[7]);

                    FileInfo file = new FileInfo(
                            fileId,
                            fileName,
                            filePath,
                            fileType,
                            fileSize,
                            status,
                            "",
                            ownerId
                    );

                    files.add(file);
                }
            }
        }

        return files;
    }

    private static String decode(String value) {

        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }
}