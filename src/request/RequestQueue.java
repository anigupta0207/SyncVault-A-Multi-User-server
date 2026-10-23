
package request;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestQueue {

    private final Queue<Request> queue;

    public RequestQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    // Add one request to the end of the FCFS queue
    public void addRequest(Request request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null."
            );
        }

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

    // View the next request without removing it
    public Request peekNextRequest() {
        return queue.peek();
    }

    // Check whether the queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Number of requests currently waiting
    public int size() {
        return queue.size();
    }

    // Add multiple requests in the supplied order
    public void addRequests(List<Request> requests) {

        if (requests == null) {
            throw new IllegalArgumentException(
                    "Request list cannot be null."
            );
        }

        for (Request request : requests) {
            addRequest(request);
        }
    }

    // Display the current queue without removing requests
    public void displayQueue() {



        System.out.println(
                "FCFS REQUEST QUEUE"
        );



        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
        } else {
            for (Request request : queue) {
                System.out.println(request);
            }
        }


    }
}
