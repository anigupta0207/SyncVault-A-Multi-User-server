package request;

public class RequestProcessor {

    private final RequestService requestService;

    public RequestProcessor() {

        requestService =
                new RequestService();
    }


    public void processRequest(Request request) {

        System.out.println(
                "\n------------------------------------------"
        );

        System.out.println(
                "Thread: "
                        + Thread.currentThread().getName()
        );

        System.out.println(
                "Attempting Request: "
                        + request.getRequestId()
        );


        // ========================================================
        // CLAIM REQUEST
        // pending -> processing
        // ========================================================

        boolean claimed =
                requestService.claimRequest(
                        request.getRequestId()
                );

        if (!claimed) {

            System.out.println(
                    "Request "
                            + request.getRequestId()
                            + " was not processed."
            );

            return;
        }


        System.out.println(
                "Request "
                        + request.getRequestId()
                        + " is now PROCESSING."
        );


        // ========================================================
        // SUBMISSION REQUEST
        // ========================================================

        if ("SUBMISSION".equalsIgnoreCase(
                request.getRequestType())) {

            System.out.println(
                    "Submission request detected."
            );

            System.out.println(
                    "Request "
                            + request.getRequestId()
                            + " is waiting for teacher approval."
            );

            System.out.println(
                    "Request remains in PROCESSING state."
            );

            System.out.println(
                    "------------------------------------------"
            );

            return;
        }


        // ========================================================
        // NORMAL REQUEST PROCESSING
        // ========================================================

        try {

            Thread.sleep(2000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "Request processing interrupted."
            );

            return;
        }


        // ========================================================
        // COMPLETE REQUEST
        // ========================================================

        boolean completed =
                requestService.updateRequestStatus(
                        request.getRequestId(),
                        "completed"
                );

        if (completed) {

            System.out.println(
                    "Request "
                            + request.getRequestId()
                            + " COMPLETED."
            );

        } else {

            System.out.println(
                    "Could not mark Request "
                            + request.getRequestId()
                            + " as completed."
            );
        }


        System.out.println(
                "------------------------------------------"
        );
    }
}