// 代码生成时间: 2025-10-06 03:47:27
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import io.javalin.Javalin;

public class FileBackupSync {

    /**
     * Main method to run the backup and sync tool.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000); // Start Javalin server on port 7000

        app.get("/backup", ctx -> {
            String sourcePath = ctx.queryParam("source"); // Get source path from query parameter
            String destinationPath = ctx.queryParam("destination"); // Get destination path from query parameter
            try {
                backupAndSyncFiles(sourcePath, destinationPath);
                ctx.result("Backup and sync completed successfully.");
            } catch (IOException e) {
                // Handle file I/O errors
                ctx.result("Error during backup and sync: " + e.getMessage());
            }
        });
    }

    /**
     * Backup and sync files between source and destination directories.
     * @param sourcePath Path to the source directory.
     * @param destinationPath Path to the destination directory.
     * @throws IOException If an I/O error occurs.
     */
    public static void backupAndSyncFiles(String sourcePath, String destinationPath) throws IOException {
        File sourceDir = new File(sourcePath);
        File destinationDir = new File(destinationPath);

        // Check if source and destination directories exist
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IllegalArgumentException("Source directory does not exist or is not a directory.");
        }
        if (!destinationDir.exists() || !destinationDir.isDirectory()) {
            throw new IllegalArgumentException("Destination directory does not exist or is not a directory.");
        }

        // Get list of files in source directory
        List<String> sourceFiles = listFiles(sourceDir);
        // Get list of files in destination directory
        List<String> destinationFiles = listFiles(destinationDir);

        // Sync files between source and destination directories
        for (String sourceFile : sourceFiles) {
            if (!destinationFiles.contains(sourceFile)) {
                // File does not exist in destination, copy it
                copyFile(sourceDir, destinationDir, sourceFile);
            } else {
                // File exists in destination, check if it needs to be updated
                if (!Files.isSameFile(Paths.get(sourceDir.getPath(), sourceFile), Paths.get(destinationDir.getPath(), sourceFile))) {
                    copyFile(sourceDir, destinationDir, sourceFile);
                }
            }
        }
    }

    /**
     * List all files in a directory.
     * @param dir Directory to list files from.
     * @return List of file names.
     */
    private static List<String> listFiles(File dir) {
        File[] files = dir.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    /**
     * Copy a file from source to destination directory.
     * @param sourceDir Source directory.
     * @param destinationDir Destination directory.
     * @param fileName Name of the file to copy.
     * @throws IOException If an I/O error occurs.
     */
    private static void copyFile(File sourceDir, File destinationDir, String fileName) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(sourceDir, fileName));
             FileOutputStream fos = new FileOutputStream(new File(destinationDir, fileName))) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
}
