package request;

import client.FileClientService;

public class SubmissionApprovalClientTest {

    public static void main(String[] args) {

        FileClientService service =
                new FileClientService();

        int requestId = 16;

        boolean result =
                service.approveSubmission(requestId);

        System.out.println(
                "TCP approval result: " + result
        );
    }
}