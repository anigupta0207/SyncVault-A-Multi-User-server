package file;

import client.FileClientService;
import java.io.IOException;

public class FileUploadTest {

    public static void main(String[] args) throws IOException {

        FileClientService service =
                new FileClientService();

        boolean result =
                service.uploadFile(
                        "C:\\Users\\Arshita\\Desktop\\test.pdf",
                        2
                );

        System.out.println(
                "Upload result: " + result
        );
    }
}