// 代码生成时间: 2025-09-17 11:36:21
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder.*;
import org.eclipse.jetty.http.HttpStatus;

public class FileSyncTool {

    /*
     * Main method to run the Javalin server.
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.routes(() -> {
            // Define routes here
            get("/status", ctx -> ctx.result("Sync tool is running"));
            post("/backup", FileSyncTool::handleBackupRequest);
        });
    }

    /*
     * Handle backup request.
     *
     * @param ctx The Javalin context.
     */
    private static void handleBackupRequest(Javalin ctx) {
        // Extract source and destination paths from request
        String sourcePath = ctx.queryParam("source");
        String destinationPath = ctx.queryParam("destination");

        try {
            // Validate paths
            if (sourcePath == null || destinationPath == null) {
                throw new IllegalArgumentException("Source and destination paths must be provided");
            }

            // Perform file backup and sync
            backupAndSyncFiles(new File(sourcePath), new File(destinationPath));

            // Return success response
            ctx.status(HttpStatus.OK_200).result("Backup and sync successful");
        } catch (IOException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500).result("Error during backup and sync: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result("Invalid request: " + e.getMessage());
        }
    }

    /*
     * Backup and sync files between two directories.
     *
     * @param sourceDir Source directory.
     * @param destinationDir Destination directory.
     * @throws IOException If an I/O error occurs.
     */
    private static void backupAndSyncFiles(File sourceDir, File destinationDir) throws IOException {
        // Create destination directory if it doesn't exist
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        // Iterate over files in source directory
        try (Stream<Path> stream = Files.walk(Paths.get(sourceDir.getPath()))) {
            stream.forEach(path -> {
                try {
                    // Construct relative path
                    Path relativePath = sourceDir.toPath().relativize(path);

                    // Construct destination path
                    Path destinationPath = destinationDir.toPath().resolve(relativePath);

                    // Ensure parent directory exists
                    Files.createDirectories(destinationPath.getParent());

                    // Copy file to destination
                    Files.copy(path, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    // Log error and continue with next file
                    System.err.println("Error copying file: " + e.getMessage());
                }
            });
        }
    }
}
