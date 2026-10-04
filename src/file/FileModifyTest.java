package file;

import client.FileClientService;

public class FileModifyTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=================================");
        System.out.println("      SYNCVAULT MODIFY TEST");
        System.out.println("=================================");

        FileClientService service = new FileClientService();

        boolean result = service.modifyFile(
                2,
                "C:\\Users\\Arshita\\Downloads\\modified_test.txt",
                2
        );

        System.out.println(
                "Modify result: " + result
        );
    }
}