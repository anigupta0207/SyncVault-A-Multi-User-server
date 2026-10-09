package file;

import client.FileClientService;

public class FileDownloadTest {

    public static void main(String[] args) throws Exception {

        FileClientService service = new FileClientService();

        String sessionToken = "YOUR_VALID_SESSION_TOKEN";

        boolean result = service.downloadFile(
                3,
                "C:\\Users\\Arshita\\Downloads",
                sessionToken
        );

        System.out.println("Download result: " + result);
    }
}