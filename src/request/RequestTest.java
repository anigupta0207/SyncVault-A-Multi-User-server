package request;

import java.util.ArrayList;
import java.util.List;

public class RequestTest {

    public static void main(String[] args) {

        RequestService requestService =
                new RequestService();

        List<Request> requests =
                requestService.getPendingRequests();

        if (requests.isEmpty()) {

            System.out.println(
                    "No pending requests available."
            );

            return;
        }


        // ==================================================
        // FCFS QUEUE TEST
        // ==================================================

        System.out.println(
                "\n=============================================="
        );

        System.out.println(
                "              FCFS QUEUE TEST"
        );

        System.out.println(
                "=============================================="
        );

        RequestQueue fcfsQueue =
                new RequestQueue();

        fcfsQueue.addRequests(requests);

        fcfsQueue.displayQueue();

        System.out.println(
                "\nFCFS PROCESSING ORDER:"
        );

        while (!fcfsQueue.isEmpty()) {

            Request request =
                    fcfsQueue.processNextRequest();

            System.out.println(
                    "Processing Request: "
                            + request.getRequestId()
            );
        }


        // ==================================================
        // PRIORITY QUEUE TEST
        // ==================================================

        System.out.println(
                "\n=============================================="
        );

        System.out.println(
                "            PRIORITY QUEUE TEST"
        );

        System.out.println(
                "=============================================="
        );

        PriorityRequestQueue priorityQueue =
                new PriorityRequestQueue();

        priorityQueue.addRequests(requests);

        priorityQueue.displayQueue();


        // ==================================================
        // REQUEST PROCESSOR
        // ==================================================

        RequestProcessor processor =
                new RequestProcessor();


        // ==================================================
        // CREATE SHARED WORKER THREADS
        // ==================================================

        List<Thread> workers =
                new ArrayList<>();

        System.out.println(
                "\n======= STARTING WORKERS ======="
        );

        int numberOfWorkers = 3;

        for (int i = 1; i <= numberOfWorkers; i++) {

            Thread worker =
                    new Thread(
                            new RequestWorker(
                                    priorityQueue,
                                    processor
                            ),
                            "Worker-" + i
                    );

            workers.add(worker);

            worker.start();
        }


        // ==================================================
        // WAIT FOR ALL WORKERS
        // ==================================================

        for (Thread worker : workers) {

            try {

                worker.join();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.out.println(
                        "Main thread interrupted."
                );
            }
        }


        System.out.println(
                "\n======= ALL WORKERS FINISHED ======="
        );
    }
}