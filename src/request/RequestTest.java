package request;

import java.util.List;

public class RequestTest {

    public static void main(String[] args) {

        RequestService requestService =
                new RequestService();


        // ==========================================
        // GET PENDING REQUESTS
        // ==========================================

        List<Request> requests =
                requestService.getPendingRequests();


        if (requests.isEmpty()) {

            System.out.println(
                    "No pending requests available."
            );

            return;
        }


        // ==========================================
        // PRIORITY QUEUE
        // ==========================================

        PriorityRequestQueue priorityQueue =
                new PriorityRequestQueue();

        priorityQueue.addRequests(requests);

        priorityQueue.displayQueue();


        // ==========================================
        // REQUEST PROCESSOR
        // ==========================================

        RequestProcessor processor =
                new RequestProcessor();


        System.out.println(
                "\n======= STARTING REQUEST PROCESSING ======="
        );


        while (!priorityQueue.isEmpty()) {

            Request request =
                    priorityQueue.processNextRequest();

            processor.processRequest(request);
        }


        System.out.println(
                "\n======= ALL REQUESTS PROCESSED ======="
        );
    }
}