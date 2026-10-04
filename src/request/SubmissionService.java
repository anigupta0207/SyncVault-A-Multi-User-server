package request;

public class SubmissionService {

    private final RequestService requestService;

    public SubmissionService() {

        requestService =
                new RequestService();
    }


    // ============================================================
    // SUBMIT FILE
    // ============================================================

    public boolean submitFile(
            int studentId,
            int fileId,
            int priority) {

        int requestId =
                submitFileWithId(
                        studentId,
                        fileId,
                        priority
                );

        return requestId != -1;
    }


    // ============================================================
    // SUBMIT FILE AND RETURN REQUEST ID
    // ============================================================

    public int submitFileWithId(
            int studentId,
            int fileId,
            int priority) {

        return requestService.createRequestWithId(
                studentId,
                fileId,
                "SUBMISSION",
                priority
        );
    }
    // ============================================================
// APPROVE SUBMISSION
// ============================================================

    public boolean approveSubmission(int requestId) {

        return requestService.approveSubmission(requestId);
    }


// ============================================================
// REJECT SUBMISSION
// ============================================================

    public boolean rejectSubmission(int requestId) {

        return requestService.rejectSubmission(requestId);
    }
}