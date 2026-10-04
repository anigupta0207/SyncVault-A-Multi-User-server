package request;

import java.util.List;

public class RequestScheduler {

    private final RequestService requestService;
    private final RequestProcessor processor;

    public RequestScheduler() {

        requestService = new RequestService();
        processor = new RequestProcessor();
    }

    // ============================================================
    // FCFS SCHEDULING
    // ============================================================

    public void runFCFS(int numberOfWorkers) {

        System.out.println(
                "\n=========================================="
        );

        System.out.println(
                "        SYNCVAULT FCFS SCHEDULER"
        );

        System.out.println(
                "=========================================="
        );

        List<Request> requests =
                requestService.getPendingRequests();

        if (requests.isEmpty()) {

            System.out.println(
                    "No pending requests."
            );

            return;
        }

        RequestQueue queue =
                new RequestQueue();

        queue.addRequests(requests);

        queue.displayQueue();

        runFCFSWorkers(
                queue,
                numberOfWorkers
        );
    }

    private void runFCFSWorkers(
            RequestQueue queue,
            int numberOfWorkers) {

        Thread[] workers =
                new Thread[numberOfWorkers];

        for (int i = 0;
             i < numberOfWorkers;
             i++) {

            RequestWorker worker =
                    new RequestWorker(
                            queue,
                            processor
                    );

            workers[i] =
                    new Thread(
                            worker,
                            "FCFS-Worker-" + (i + 1)
                    );

            workers[i].start();
        }

        waitForWorkers(workers);
    }

    // ============================================================
    // PRIORITY SCHEDULING
    // ============================================================

    public void runPriority(int numberOfWorkers) {

        System.out.println(
                "\n=========================================="
        );

        System.out.println(
                "      SYNCVAULT PRIORITY SCHEDULER"
        );

        System.out.println(
                "=========================================="
        );

        List<Request> requests =
                requestService.getPendingRequests();

        if (requests.isEmpty()) {

            System.out.println(
                    "No pending requests."
            );

            return;
        }

        PriorityRequestQueue queue =
                new PriorityRequestQueue();

        queue.addRequests(requests);

        queue.displayQueue();

        runPriorityWorkers(
                queue,
                numberOfWorkers
        );
    }

    private void runPriorityWorkers(
            PriorityRequestQueue queue,
            int numberOfWorkers) {

        Thread[] workers =
                new Thread[numberOfWorkers];

        for (int i = 0;
             i < numberOfWorkers;
             i++) {

            RequestWorker worker =
                    new RequestWorker(
                            queue,
                            processor
                    );

            workers[i] =
                    new Thread(
                            worker,
                            "Priority-Worker-" + (i + 1)
                    );

            workers[i].start();
        }

        waitForWorkers(workers);
    }

    // ============================================================
    // WAIT FOR WORKERS
    // ============================================================

    private void waitForWorkers(
            Thread[] workers) {

        for (Thread worker : workers) {

            try {

                worker.join();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.out.println(
                        "Scheduler interrupted."
                );

                return;
            }
        }

        System.out.println(
                "\nAll workers finished."
        );
    }
}