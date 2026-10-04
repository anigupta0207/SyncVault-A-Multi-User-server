package file;

import client.FileClientService;

public class FileShareClientTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=================================");
        System.out.println("     SYNCVAULT TCP SHARE TEST");
        System.out.println("=================================");

        FileClientService service =
                new FileClientService();

        boolean result =
                service.shareFile(
                        2,      // file ID
                        2,      // shared by
                        1,      // shared with
                        "view"
                );

        System.out.println(
                "Share result: " + result
        );
    }
}