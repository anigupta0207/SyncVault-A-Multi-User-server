package file;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileTest {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       SYNCVAULT FILE TEST");
        System.out.println("=================================");

        FileService fileService =
                new FileService();

        // CREATing test file

        String testFilePath =
                "test_upload.txt";

        try {

            Files.writeString(
                    Paths.get(testFilePath),
                    "This is a test file for SyncVault."
            );

            System.out.println(
                    "\nTest file created: "
                            + testFilePath
            );

        } catch (Exception e) {

            System.out.println(
                    "Could not create test file."
            );

            e.printStackTrace();

            return;
        }

        // file upload


        System.out.println(
                "\n======= UPLOADING FILE ======="
        );

        boolean uploaded =
                fileService.uploadFile(
                        testFilePath,
                        "test_upload.txt",
                        2
                );

        if (!uploaded) {

            System.out.println(
                    "File upload failed."
            );

            return;
        }


        //  VIEW FILE

        System.out.println(
                "\n======= FILES IN SYNCVAULT ======="
        );

        fileService.viewAllFiles();


        //  VERIFY FILE OBJECTS 


        List<FileInfo> files =
                fileService.getAllFiles();

        System.out.println(
                "\nTotal files stored: "
                        + files.size()
        );

        System.out.println(
                "\n================================="
        );

        System.out.println(
                "       FILE TEST COMPLETED"
        );

        System.out.println(
                "================================="
        );
    }
}