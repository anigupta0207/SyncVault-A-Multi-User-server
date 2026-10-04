package file;

public class FileShareTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=================================");
        System.out.println("       SYNCVAULT SHARE TEST");
        System.out.println("=================================");

        FileShareService service =
                new FileShareService();

        boolean result =
                service.shareFile(
                        2,      // file ID
                        2,      // shared by
                        1,      // shared with
                        "view"  // permission
                );

        System.out.println(
                "Share result: " + result
        );

        if (result) {
            System.out.println(
                    "File sharing successful."
            );
        }
    }
}