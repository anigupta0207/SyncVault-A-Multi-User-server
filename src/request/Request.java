package request;

public class Request {

    private int requestId;
    private int userId;
    private Integer fileId;
    private String requestType;
    private String status;
    private int priority;
    private String requestTime;

    public Request(
            int requestId,
            int userId,
            Integer fileId,
            String requestType,
            String status,
            int priority,
            String requestTime) {

        this.requestId = requestId;
        this.userId = userId;
        this.fileId = fileId;
        this.requestType = requestType;
        this.status = status;
        this.priority = priority;
        this.requestTime = requestTime;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getFileId() {
        return fileId;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }

    public String getRequestTime() {
        return requestTime;
    }

    @Override
    public String toString() {

        return "Request{" +
                "requestId=" + requestId +
                ", userId=" + userId +
                ", fileId=" + fileId +
                ", requestType='" + requestType + '\'' +
                ", status='" + status + '\'' +
                ", priority=" + priority +
                ", requestTime='" + requestTime + '\'' +
                '}';
    }
}