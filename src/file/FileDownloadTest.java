package file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadTest {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("     SYNCVAULT DOWNLOAD TEST");
        System.out.println("=================================");

        FileService fileService =
                new FileService();


        // FILE TO DOWNLOAD

        int fileId = 1;

        String downloadDirectory =
                "downloads";


        // CREATE DOWNLOAD DIRECTORY

        try {

            Path directory =
                    Paths.get(downloadDirectory);

            if (!Files.exists(directory)) {

                Files.createDirectories(directory);
            }

        } catch (Exception e) {

            System.out.println(
                    "Could not create download directory."
            );

            e.printStackTrace();

            return;
        }


        // =================================================
        // DOWNLOAD FILE
        // =================================================

        System.out.println(
                "\n======= DOWNLOADING FILE ======="
        );

        boolean downloaded =
                fileService.downloadFile(
                        fileId,
                        downloadDirectory
                );

        // RESULT

        if (downloaded) {

            System.out.println(
                    "\nDownload test successful."
            );

        } else {

            System.out.println(
                    "\nDownload test failed."
            );

            return;
        }

        // VERIFY DOWNLOADED FILE

        Path downloadedFile =
                Paths.get(
                        downloadDirectory,
                        "test_upload.txt"
                );


        if (Files.exists(downloadedFile)) {

            System.out.println(
                    "Downloaded file exists."
            );

            try {

                String content =
                        Files.readString(
                                downloadedFile
                        );

                System.out.println(
                        "\nDownloaded file content:"
                );

                System.out.println(
                        content
                );

            } catch (Exception e) {

                System.out.println(
                        "Could not read downloaded file."
                );

                e.printStackTrace();
            }

        } else {

            System.out.println(
                    "Downloaded file was not found."
            );
        }


        System.out.println(
                "\n================================="
        );

        System.out.println(
                "     DOWNLOAD TEST COMPLETED"
        );

        System.out.println(
                "================================="
        );
    }
}