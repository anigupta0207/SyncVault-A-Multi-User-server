package request;

public class RequestWorker implements Runnable {

    private final RequestQueue fcfsQueue;
    private final PriorityRequestQueue priorityQueue;

    private final RequestProcessor processor;


    // ============================================================
    // FCFS CONSTRUCTOR
    // ============================================================

    public RequestWorker(
            RequestQueue queue,
            RequestProcessor processor) {

        this.fcfsQueue = queue;
        this.priorityQueue = null;
        this.processor = processor;
    }


    // ============================================================
    // PRIORITY CONSTRUCTOR
    // ============================================================

    public RequestWorker(
            PriorityRequestQueue queue,
            RequestProcessor processor) {

        this.fcfsQueue = null;
        this.priorityQueue = queue;
        this.processor = processor;
    }


    @Override
    public void run() {

        String threadName =
                Thread.currentThread().getName();

        System.out.println(
                "Worker started | Thread: "
                        + threadName
        );


        while (true) {

            Request request;


            // ====================================================
            // PRIORITY QUEUE
            // ====================================================

            if (priorityQueue != null) {

                request =
                        priorityQueue.processNextRequest();

            }

            // ====================================================
            // FCFS QUEUE
            // ====================================================

            else {

                request =
                        fcfsQueue.processNextRequest();
            }


            // ====================================================
            // NO REQUESTS LEFT
            // ====================================================

            if (request == null) {

                break;
            }


            System.out.println(
                    "Thread "
                            + threadName
                            + " picked Request "
                            + request.getRequestId()
                            + " | Type: "
                            + request.getRequestType()
                            + " | Priority: "
                            + request.getPriority()
            );


            // ====================================================
            // PROCESS REQUEST
            // ====================================================

            processor.processRequest(request);
        }


        System.out.println(
                "Worker finished | Thread: "
                        + threadName
        );
    }
}