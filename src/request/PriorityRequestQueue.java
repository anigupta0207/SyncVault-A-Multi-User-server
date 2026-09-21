package request;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.List;

public class PriorityRequestQueue {

    private final PriorityQueue<Request> queue;

    public PriorityRequestQueue() {

        queue = new PriorityQueue<>(
                Comparator
                        .comparingInt(Request::getPriority)
                        .thenComparingInt(Request::getRequestId)
        );
    }

    // Add one request
    public void addRequest(Request request) {

        queue.offer(request);

        System.out.println(
                "Request added to Priority Queue: "
                        + request.getRequestId()
                        + " | Priority: "
                        + request.getPriority()
        );
    }

    // Add multiple requests
    public void addRequests(List<Request> requests) {

        for (Request request : requests) {

            addRequest(request);
        }
    }

    // Process highest-priority request
    public Request processNextRequest() {

        return queue.poll();
    }

    // View next request without removing it
    public Request peekNextRequest() {

        return queue.peek();
    }

    // Check if queue is empty
    public boolean isEmpty() {

        return queue.isEmpty();
    }

    // Number of waiting requests
    public int size() {

        return queue.size();
    }

    // Display queue
    public void displayQueue() {

        System.out.println(
                "\n=============================================="
        );

        System.out.println(
                "          PRIORITY REQUEST QUEUE"
        );

        System.out.println(
                "=============================================="
        );

        if (queue.isEmpty()) {

            System.out.println("Queue is empty.");

            return;
        }

        // Do not use poll() here because it would destroy the queue.
        PriorityQueue<Request> tempQueue =
                new PriorityQueue<>(queue);

        while (!tempQueue.isEmpty()) {

            Request request =
                    tempQueue.poll();

            System.out.println(
                    "Request ID: "
                            + request.getRequestId()
                            + " | Type: "
                            + request.getRequestType()
                            + " | Priority: "
                            + request.getPriority()
            );
        }

        System.out.println(
                "=============================================="
        );
    }
}