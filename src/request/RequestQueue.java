package request;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RequestQueue {

    private final Queue<Request> queue;

    public RequestQueue() {
        queue = new LinkedList<>();
    }

    // Add a request to the end of the queue
    public void addRequest(Request request) {

        queue.offer(request);

        System.out.println(
                "Request added to FCFS queue: "
                        + request.getRequestId()
        );
    }

    // Remove the request that arrived first
    public Request processNextRequest() {

        return queue.poll();
    }

    // See the next request without removing it
    public Request peekNextRequest() {

        return queue.peek();
    }

    // Check whether queue is empty
    public boolean isEmpty() {

        return queue.isEmpty();
    }

    // Number of requests waiting
    public int size() {

        return queue.size();
    }

    // Add multiple requests
    public void addRequests(List<Request> requests) {

        for (Request request : requests) {

            addRequest(request);
        }
    }

    // Display current queue
    public void displayQueue() {

        System.out.println(
                "\n=============================================="
        );

        System.out.println(
                "             FCFS REQUEST QUEUE"
        );

        System.out.println(
                "=============================================="
        );

        if (queue.isEmpty()) {

            System.out.println("Queue is empty.");

            return;
        }

        for (Request request : queue) {

            System.out.println(request);
        }

        System.out.println(
                "=============================================="
        );
    }
}