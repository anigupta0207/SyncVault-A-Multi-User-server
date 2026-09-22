package request;

public class RequestWorker implements Runnable {

    private final Request request;
    private final RequestProcessor processor;

    public RequestWorker(Request request,
                         RequestProcessor processor) {

        this.request = request;
        this.processor = processor;
    }

    @Override
    public void run() {

        System.out.println(
                "Worker started | Thread: "
                        + Thread.currentThread().getName()
                        + " | Request: "
                        + request.getRequestId()
        );

        processor.processRequest(request);

        System.out.println(
                "Worker finished | Thread: "
                        + Thread.currentThread().getName()
                        + " | Request: "
                        + request.getRequestId()
        );
    }
}