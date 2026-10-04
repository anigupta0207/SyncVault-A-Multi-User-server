package request;

public class SubmissionService {

    private final RequestService requestService;

    public SubmissionService() {
        this.requestService = new RequestService();
    }

    public boolean submitFile(
            int studentId,
            int fileId,
            int priority) {

        return requestService.createRequest(
                studentId,
                fileId,
                "SUBMISSION",
                priority
        );
    }
}