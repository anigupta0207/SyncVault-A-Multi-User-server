
package file;

public class SharedResource {

    private final int resourceId;
    private final int fileId;
    private final String fileName;
    private final String fileType;
    private final long fileSize;
    private final int ownerId;
    private final String resourceType;
    private final String title;
    private final String description;
    private final String dueDate;

    public SharedResource(
            int resourceId,
            int fileId,
            String fileName,
            String fileType,
            long fileSize,
            int ownerId,
            String resourceType,
            String title,
            String description,
            String dueDate) {

        this.resourceId = resourceId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.ownerId = ownerId;
        this.resourceType = resourceType;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }
}
