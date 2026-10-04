package file;

public class FileShare {

    private int shareId;
    private int fileId;
    private int sharedBy;
    private int sharedWith;
    private String permission;
    private String sharedAt;

    private String fileName;
    private long fileSize;
    private String fileType;

    public FileShare(
            int shareId,
            int fileId,
            int sharedBy,
            int sharedWith,
            String permission,
            String sharedAt,
            String fileName,
            long fileSize,
            String fileType) {

        this.shareId = shareId;
        this.fileId = fileId;
        this.sharedBy = sharedBy;
        this.sharedWith = sharedWith;
        this.permission = permission;
        this.sharedAt = sharedAt;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public int getShareId() {
        return shareId;
    }

    public int getFileId() {
        return fileId;
    }

    public int getSharedBy() {
        return sharedBy;
    }

    public int getSharedWith() {
        return sharedWith;
    }

    public String getPermission() {
        return permission;
    }

    public String getSharedAt() {
        return sharedAt;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }
}