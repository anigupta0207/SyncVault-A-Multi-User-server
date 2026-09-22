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
        // PRIORITY QUEUE

        PriorityRequestQueue priorityQueue =
                new PriorityRequestQueue();

        priorityQueue.addRequests(requests);

        priorityQueue.displayQueue();


        // REQUEST PROCESSOR

        RequestProcessor processor =
                new RequestProcessor();


        // CREATE WORKER THREADS

        List<Thread> workers =
                new ArrayList<>();


        System.out.println(
                "\n======= STARTING WORKERS ======="
        );


        while (!priorityQueue.isEmpty()) {

            Request request =
                    priorityQueue.processNextRequest();

            Thread worker =
                    new Thread(
                            new RequestWorker(
                                    request,
                                    processor
                            )
                    );

            workers.add(worker);

            worker.start();
        }

        // WAIT FOR ALL WORKERS // therading

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