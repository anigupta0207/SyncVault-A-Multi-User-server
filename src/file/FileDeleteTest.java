package file;

import client.FileClientService;

public class FileDeleteTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=================================");
        System.out.println("      SYNCVAULT DELETE TEST");
        System.out.println("=================================");

        FileClientService service =
                new FileClientService();

        boolean result =
                service.deleteFile(3);

        System.out.println(
                "Delete result: " + result
        );
    }
}