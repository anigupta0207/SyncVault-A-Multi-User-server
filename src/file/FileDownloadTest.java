package file;

import client.FileClientService;

public class FileDownloadTest {

    public static void main(String[] args) throws Exception {

        FileClientService service =
                new FileClientService();

        boolean result =
                service.downloadFile(
                        1,
                        "C:\\Users\\Arshita\\Downloads"
                );

        System.out.println(
                "Download result: " + result
        );
    }
}