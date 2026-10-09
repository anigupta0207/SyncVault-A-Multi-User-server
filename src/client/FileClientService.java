package client;

import file.FileInfo;
import file.FileShare;
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
            String destinationDirectory,
            String sessionToken
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

                if (sessionToken == null || sessionToken.isBlank()) {
                    throw new IOException("You must sign in before downloading.");
                }

                output.println(
                        "DOWNLOAD_FILE" + "\t" +
                                fileId + "\t" +
                                encode(sessionToken)
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
    public boolean deleteFile(int fileId, String sessionToken)
            throws IOException {

        if (sessionToken == null || sessionToken.isBlank()) {
            throw new IllegalArgumentException(
                    "A valid session token is required."
            );
        }

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(SERVER_HOST, SERVER_PORT),
                    5000
            );

            socket.setSoTimeout(10000);

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

                output.println(
                        "DELETE_FILE\t" + fileId + "\t"
                                + java.util.Base64.getUrlEncoder()
                                .withoutPadding()
                                .encodeToString(
                                        sessionToken.getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                )
                );

                String response = input.readLine();

                if ("DELETE_OK".equals(response)) {
                    System.out.println("File deleted successfully.");
                    return true;
                }

                System.out.println("Delete failed: " + response);
                return false;
            }
        }
    }
    public boolean modifyFile(int fileId, String newFilePath, int userId) throws IOException {

        File file = new File(newFilePath);

        if (!file.exists() || !file.isFile()) {
            System.out.println("Replacement file not found.");
            return false;
        }

        long fileSize = file.length();

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(SERVER_HOST, SERVER_PORT),
                    5000
            );

            socket.setSoTimeout(15000);

            try (
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    StandardCharsets.UTF_8));

                    PrintWriter output = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(),
                                    StandardCharsets.UTF_8),
                            true);

                    FileInputStream fileInput = new FileInputStream(file)
            ) {

                // Start modify request
                output.println(
                        "MODIFY_FILE" + "\t" +
                                fileId + "\t" +
                                userId + "\t" +
                                fileSize
                );

                // Send file in chunks
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = fileInput.read(buffer)) != -1) {

                    String encoded = Base64.getEncoder()
                            .encodeToString(
                                    java.util.Arrays.copyOf(buffer, bytesRead)
                            );

                    output.println("DATA" + "\t" + encoded);
                }

                output.println("MODIFY_END");

                String response = input.readLine();

                if ("MODIFY_OK".equals(response)) {
                    System.out.println("File modified successfully.");
                    return true;
                }

                System.out.println("Modify failed: " + response);
                return false;
            }
        }
    }
    public boolean shareFile(
            int fileId,
            int sharedBy,
            int sharedWith,
            String permission) throws IOException {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            SERVER_HOST,
                            SERVER_PORT
                    ),
                    5000
            );

            socket.setSoTimeout(10000);

            try (
                    PrintWriter output =
                            new PrintWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream(),
                                            StandardCharsets.UTF_8
                                    ),
                                    true
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
                        "SHARE_FILE" + "\t" +
                                fileId + "\t" +
                                sharedBy + "\t" +
                                sharedWith + "\t" +
                                permission
                );

                String response = input.readLine();

                if ("SHARE_OK".equals(response)) {

                    System.out.println(
                            "File shared successfully."
                    );

                    return true;
                }

                System.out.println(
                        "Share failed: " + response
                );

                return false;
            }
        }
    }
    public List<FileShare> getSharedFiles(int userId)
            throws IOException {

        List<FileShare> shares =
                new ArrayList<>();

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            SERVER_HOST,
                            SERVER_PORT
                    ),
                    5000
            );

            socket.setSoTimeout(10000);

            try (
                    PrintWriter output =
                            new PrintWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream(),
                                            StandardCharsets.UTF_8
                                    ),
                                    true
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
                        "GET_SHARED_FILES"
                                + "\t"
                                + userId
                );

                String line;

                while ((line = input.readLine()) != null) {

                    if ("SHARED_FILES_END".equals(line)) {
                        break;
                    }

                    if (line.startsWith("SHARED_FILE\t")) {

                        String[] parts =
                                line.split("\t", -1);

                        if (parts.length != 10) {
                            continue;
                        }

                        FileShare share =
                                new FileShare(
                                        Integer.parseInt(parts[1]),
                                        Integer.parseInt(parts[2]),
                                        Integer.parseInt(parts[3]),
                                        Integer.parseInt(parts[4]),
                                        decode(parts[5]),
                                        decode(parts[6]),
                                        decode(parts[7]),
                                        Long.parseLong(parts[8]),
                                        decode(parts[9])
                                );

                        shares.add(share);

                        shares.add(share);
                    }

                    if (line.startsWith(
                            "SHARED_FILES_ERROR")) {

                        System.out.println(
                                "Error: " + line
                        );

                        break;
                    }
                }
            }
        }

        return shares;
    }
    public boolean submitFile(
            int studentId,
            int fileId,
            int priority) {

        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);

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

            output.println(
                    "SUBMIT_FILE"
                            + "\t"
                            + studentId
                            + "\t"
                            + fileId
                            + "\t"
                            + priority
            );

            String response = input.readLine();

            if ("SUBMIT_OK".equals(response)) {

                System.out.println(
                        "File submitted successfully."
                );

                return true;
            }

            if (response != null &&
                    response.startsWith("SUBMIT_ERROR")) {

                System.out.println(
                        "Submission failed: "
                                + response
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Connection error while submitting file."
            );

            e.printStackTrace();
        }

        return false;
    }
// ============================================================
// APPROVE SUBMISSION
// ============================================================

    public boolean approveSubmission(int requestId) {

        try (
                Socket socket =
                        new Socket(SERVER_HOST, SERVER_PORT);

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

            output.println(
                    "APPROVE_SUBMISSION"
                            + "\t"
                            + requestId
            );

            String response = input.readLine();

            if ("APPROVE_OK".equals(response)) {

                System.out.println(
                        "Submission approved successfully."
                );

                return true;
            }

            if (response != null &&
                    response.startsWith("APPROVE_ERROR")) {

                System.out.println(
                        "Approval failed: "
                                + response
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Connection error while approving submission."
            );

            e.printStackTrace();
        }

        return false;
    }


// ============================================================
// REJECT SUBMISSION
// ============================================================

    public boolean rejectSubmission(int requestId) {

        try (
                Socket socket =
                        new Socket(SERVER_HOST, SERVER_PORT);

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

            output.println(
                    "REJECT_SUBMISSION"
                            + "\t"
                            + requestId
            );

            String response = input.readLine();

            if ("REJECT_OK".equals(response)) {

                System.out.println(
                        "Submission rejected successfully."
                );

                return true;
            }

            if (response != null &&
                    response.startsWith("REJECT_ERROR")) {

                System.out.println(
                        "Rejection failed: "
                                + response
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Connection error while rejecting submission."
            );

            e.printStackTrace();
        }

        return false;
    }
}