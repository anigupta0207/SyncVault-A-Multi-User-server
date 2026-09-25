package file;

public class FileDeleteTest {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       SYNCVAULT DELETE TEST");
        System.out.println("=================================");

        FileService fileService =
                new FileService();

        // File ID we want to delete
        int fileId = 3;

        System.out.println(
                "\n======= DELETING FILE ======="
        );

        boolean deleted =
                fileService.deleteFile(fileId);

        if (deleted) {

            System.out.println(
                    "\nDelete test successful."
            );

        } else {

            System.out.println(
                    "\nDelete test failed."
            );
        }

        System.out.println(
                "\n======= CURRENT FILES ======="
        );

        fileService.viewAllFiles();

        System.out.println(
                "\n================================="
        );

        System.out.println(
                "       DELETE TEST COMPLETED"
        );

        System.out.println(
                "================================="
        );
    }
}