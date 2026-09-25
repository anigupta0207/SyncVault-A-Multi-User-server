package file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileModifyTest {

    public static void main(String[] args) {

        FileService fileService = new FileService();

        // Use an ACTIVE file
        int fileId = 1;

        // Create a new file that will replace the existing file
        String newFilePath = "modified_content.txt";

        try {
            Files.writeString(
                    Paths.get(newFilePath),
                    "This is the MODIFIED content of the SyncVault file."
            );

            System.out.println("New modification file created.");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Modify existing file
        boolean modified = fileService.modifyFile(
                fileId,
                newFilePath,
                2       // owner/user ID
        );

        if (modified) {
            System.out.println("\nModify test successful.");
        } else {
            System.out.println("\nModify test failed.");
        }
    }
}