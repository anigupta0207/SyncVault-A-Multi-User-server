package client;

import request.Request;

import java.util.List;

public class RequestClientTest {

    public static void main(String[] args) {

        try {

            RequestClientService service =
                    new RequestClientService();

            System.out.println(
                    "Creating test request..."
            );

            boolean created =
                    service.createRequest(
                            2,          // user ID
                            null,       // file ID
                            "DOWNLOAD",
                            3           // priority
                    );

            if (created) {

                System.out.println(
                        "CREATE_REQUEST successful!"
                );

            } else {

                System.out.println(
                        "CREATE_REQUEST failed."
                );

                return;
            }

            System.out.println();
            System.out.println(
                    "Fetching pending requests..."
            );

            List<Request> requests =
                    service.getPendingRequests();

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "   PENDING REQUESTS"
            );

            System.out.println(
                    "================================="
            );

            for (Request request : requests) {

                System.out.println(
                        "Request ID : "
                                + request.getRequestId()
                );

                System.out.println(
                        "User ID    : "
                                + request.getUserId()
                );

                System.out.println(
                        "File ID    : "
                                + request.getFileId()
                );

                System.out.println(
                        "Type       : "
                                + request.getRequestType()
                );

                System.out.println(
                        "Status     : "
                                + request.getStatus()
                );

                System.out.println(
                        "Priority   : "
                                + request.getPriority()
                );

                System.out.println(
                        "Time       : "
                                + request.getRequestTime()
                );

                System.out.println(
                        "---------------------------------"
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Request test failed."
            );

            e.printStackTrace();
        }
    }
}