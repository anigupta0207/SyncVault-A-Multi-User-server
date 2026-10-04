package client;

import file.FileInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static String encode(String value) {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8)
                );
    }
    private static String decode(String value) {

        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }
    public boolean uploadFile(String filePath, int ownerId) throws IOException {

        Path sourceFile = Paths.get(filePath);

        if (!Files.exists(sourceFile) || !Files.isRegularFile(sourceFile)) {
            System.out.println("File does not exist: " + filePath);
            return false;
        }

        String fileName = sourceFile.getFileName().toString();
        long fileSize = Files.size(sourceFile);

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(SERVER_HOST, SERVER_PORT),
                    5000
            );

            socket.setSoTimeout(30000);

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

                /*
                 * Send upload metadata.
                 */
                output.println(
                        "UPLOAD_FILE" + "\t" +
                                ownerId + "\t" +
                                encode(fileName) + "\t" +
                                fileSize
                );

                /*
                 * Send file in chunks.
                 */
                try (InputStream fileInput =
                             Files.newInputStream(sourceFile)) {

                    byte[] buffer = new byte[8192];

                    int bytesRead;

                    while ((bytesRead = fileInput.read(buffer)) != -1) {

                        String encodedChunk =
                                Base64.getEncoder()
                                        .encodeToString(
                                                java.util.Arrays.copyOf(
                                                        buffer,
                                                        bytesRead
                                                )
                                        );

                        output.println(
                                "DATA" + "\t" + encodedChunk
                        );
                    }
                }

                /*
                 * Tell server upload is complete.
                 */
                output.println("UPLOAD_END");

                String response = input.readLine();

                if ("UPLOAD_OK".equals(response)) {
                    return true;
                }

                System.out.println(
                        "Upload failed: " + response
                );

                return false;
            }
        }
    }
    public boolean downloadFile(
            int fileId,
            String destinationDirectory
    ) throws IOException {

        Path destination =
                Paths.get(destinationDirectory);

        Files.createDirectories(destination);

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            SERVER_HOST,
                            SERVER_PORT
                    ),
                    5000
            );

            socket.setSoTimeout(30000);

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

                output.println(
                        "DOWNLOAD_FILE" + "\t" + fileId
                );

                String response =
                        input.readLine();

                if (response == null) {
                    return false;
                }

                if (response.startsWith("DOWNLOAD_ERROR")) {

                    System.out.println(
                            "Download failed: " + response
                    );

                    return false;
                }

                String[] header =
                        response.split("\t", -1);

                if (header.length != 3 ||
                        !header[0].equals("DOWNLOAD_OK")) {

                    System.out.println(
                            "Invalid download response."
                    );

                    return false;
                }

                String fileName =
                        decode(header[1]);

                long expectedSize =
                        Long.parseLong(header[2]);

                Path outputFile =
                        destination.resolve(fileName);

                long receivedSize = 0;

                try (OutputStream fileOutput =
                             Files.newOutputStream(
                                     outputFile
                             )) {

                    while (true) {

                        String line =
                                input.readLine();

                        if (line == null) {
                            throw new IOException(
                                    "Server disconnected during download."
                            );
                        }

                        if (line.equals("DOWNLOAD_END")) {
                            break;
                        }

                        if (!line.startsWith("DATA" + "\t")) {
                            throw new IOException(
                                    "Invalid download data."
                            );
                        }

                        String encodedData =
                                line.substring(5);

                        byte[] chunk =
                                Base64.getDecoder()
                                        .decode(encodedData);

                        fileOutput.write(chunk);

                        receivedSize += chunk.length;
                    }
                }

                if (receivedSize != expectedSize) {

                    Files.deleteIfExists(outputFile);

                    System.out.println(
                            "Download failed: file size mismatch."
                    );

                    return false;
                }

                System.out.println(
                        "File downloaded successfully: "
                                + outputFile
                );

                return true;
            }
        }
    }
}