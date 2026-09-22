package file;

public class FileInfo {

    private int fileId;
    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private String status;
    private String uploadDate;
    private int ownerId;

    public FileInfo(
            int fileId,
            String fileName,
            String filePath,
            String fileType,
            long fileSize,
            String status,
            String uploadDate,
            int ownerId) {

        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.status = status;
        this.uploadDate = uploadDate;
        this.ownerId = ownerId;
    }

    public int getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getStatus() {
        return status;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", status='" + status + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }
}