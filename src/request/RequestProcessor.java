package request;

public class RequestProcessor {

    private final RequestService requestService;

    public RequestProcessor() {

        requestService =
                new RequestService();
    }


    // ==========================================
    // PROCESS ONE REQUEST
    // ==========================================

    public void processRequest(Request request) {

        System.out.println(
                "\n------------------------------------------"
        );

        System.out.println(
                "Starting Request: "
                        + request.getRequestId()
        );

        System.out.println(
                "Type: "
                        + request.getRequestType()
        );

        System.out.println(
                "User ID: "
                        + request.getUserId()
        );

        System.out.println(
                "Priority: "
                        + request.getPriority()
        );


        // --------------------------------------
        // pending → processing
        // --------------------------------------

        boolean started =
                requestService.updateRequestStatus(
                        request.getRequestId(),
                        "processing"
                );

        if (!started) {

            System.out.println(
                    "Could not start request."
            );

            return;
        }

        System.out.println(
                "Status: processing"
        );


        // --------------------------------------
        // Simulate request processing
        // --------------------------------------

        try {

            Thread.sleep(2000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "Request processing interrupted."
            );

            return;
        }


        // --------------------------------------
        // processing → completed
        // --------------------------------------

        boolean completed =
                requestService.updateRequestStatus(
                        request.getRequestId(),
                        "completed"
                );

        if (completed) {

            System.out.println(
                    "Status: completed"
            );

        } else {

            System.out.println(
                    "Request processing completed, " +
                            "but database update failed."
            );
        }

        System.out.println(
                "------------------------------------------"
        );
    }
}