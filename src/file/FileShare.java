package file;

public class FileShare {

    private int shareId;
    private int fileId;
    private int sharedBy;
    private int sharedWith;
    private String permission;
    private String sharedAt;

    public FileShare(
            int shareId,
            int fileId,
            int sharedBy,
            int sharedWith,
            String permission,
            String sharedAt) {

        this.shareId = shareId;
        this.fileId = fileId;
        this.sharedBy = sharedBy;
        this.sharedWith = sharedWith;
        this.permission = permission;
        this.sharedAt = sharedAt;
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
}