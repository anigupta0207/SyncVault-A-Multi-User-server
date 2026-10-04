package file;

import client.FileClientService;

import java.util.List;

public class FileSharedFilesTest {

    public static void main(String[] args)
            throws Exception {

        System.out.println("=================================");
        System.out.println("   SYNCVAULT SHARED FILES TEST");
        System.out.println("=================================");

        FileClientService service =
                new FileClientService();

        // User 1 is the receiver
        List<FileShare> shares =
                service.getSharedFiles(1);

        System.out.println(
                "\nShared files for User 1:"
        );

        if (shares.isEmpty()) {

            System.out.println(
                    "No shared files found."
            );

        } else {

            for (FileShare share : shares) {

                System.out.println(
                        "---------------------------------"
                );

                System.out.println(
                        "Share ID: "
                                + share.getShareId()
                );

                System.out.println(
                        "File ID: "
                                + share.getFileId()
                );

                System.out.println(
                        "Shared By: "
                                + share.getSharedBy()
                );

                System.out.println(
                        "Permission: "
                                + share.getPermission()
                );

                System.out.println(
                        "Shared At: "
                                + share.getSharedAt()
                );
                System.out.println(
                        "File Name: " + share.getFileName()
                );

                System.out.println(
                        "File Size: " + share.getFileSize()
                );

                System.out.println(
                        "File Type: " + share.getFileType()
                );
            }
        }
    }
}