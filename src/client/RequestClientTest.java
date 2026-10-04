package client;

import request.Request;

import java.util.List;

public class RequestClientTest {

    public static void main(String[] args) {

        try {

            RequestClientService service =
                    new RequestClientService();

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

            if (requests.isEmpty()) {

                System.out.println(
                        "No pending requests found."
                );

            } else {

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
            }
//comment
        } catch (Exception e) {

            System.out.println(
                    "Failed to retrieve requests."
            );

            e.printStackTrace();
        }
    }
}