package request;

import client.FileClientService;

public class SubmissionClientTest {

    public static void main(String[] args) {

        FileClientService service =
                new FileClientService();

        int studentId = 1;
        int fileId = 2;
        int priority = 1;

        boolean result = service.submitFile(
                studentId,
                fileId,
                priority
        );

        System.out.println(
                "Submission result: " + result
        );
    }
}