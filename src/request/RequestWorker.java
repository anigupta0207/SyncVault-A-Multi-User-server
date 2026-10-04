package request;

public class RequestWorker implements Runnable {

    private final PriorityRequestQueue queue;
    private final RequestProcessor processor;

    public RequestWorker(
            PriorityRequestQueue queue,
            RequestProcessor processor) {

        this.queue = queue;
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

            // Safely take one request from shared queue
            Request request =
                    queue.processNextRequest();

            // No requests left
            if (request == null) {

                break;
            }

            System.out.println(
                    "Thread "
                            + threadName
                            + " picked Request "
                            + request.getRequestId()
            );

            processor.processRequest(request);
        }

        System.out.println(
                "Worker finished | Thread: "
                        + threadName
        );
    }
}