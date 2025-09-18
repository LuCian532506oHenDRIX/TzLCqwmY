// 代码生成时间: 2025-09-18 18:36:56
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Javalin application that acts as a folder structure organizer.
 * It allows users to upload a folder which will then be organized by sorting
 * its contents alphabetically and recursively.
 */
public class FolderOrganizer {

    private static final String UPLOAD_PATH = "upload/";
    private static final String ORGANIZED_PATH = "organized/";

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.post("/organize", FolderOrganizer::organizeFolder);
    }

    private static void organizeFolder(Context ctx) throws IOException {
        List<String> filePaths = ctx.bodyAsBytesList().stream()
            .map(bytes -> {
                try {
                    // Save the uploaded file to the upload directory
                    return saveUploadedFile(bytes);
                } catch (IOException ex) {
                    ctx.status(500).result("Error saving uploaded file: " + ex.getMessage());
                    return null;
                }
            })
            .filter(file -> file != null && new File(file).isDirectory())
            .flatMap(folderPath -> {
                try {
                    // Organize the folder and return the organized folder path
                    return organizeFolderRecursive(folderPath).stream();
                } catch (IOException ex) {
                    ctx.status(500).result("Error organizing folder: " + ex.getMessage());
                    return Stream.empty();
                }
            })
            .collect(Collectors.toList());

        if (filePaths.isEmpty()) {
            ctx.status(404).result("No organized folders found.");
        } else {
            ctx.json(filePaths);
        }
    }

    private static String saveUploadedFile(byte[] bytes) throws IOException {
        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File uploadedFile = new File(uploadDir, System.currentTimeMillis() + ".zip");
        Files.write(uploadedFile.toPath(), bytes);

        return uploadedFile.getAbsolutePath();
    }

    private static Stream<String> organizeFolderRecursive(String folderPath) throws IOException {
        Path folder = Paths.get(folderPath);
        if (!Files.isDirectory(folder)) {
            throw new IOException("Provided path is not a directory.");
        }

        // Create a new directory for the organized contents
        Path organizedDir = Paths.get(ORGANIZED_PATH, folder.getFileName().toString());
        if (!Files.exists(organizedDir)) {
            Files.createDirectories(organizedDir);
        }

        // Recursively sort and move files into the organized directory
        return Files.walk(folder)
            .sorted(Comparator.comparing(Path::toString))
            .map(path -> {
                if (Files.isDirectory(path)) {
                    return organizeFolderRecursive(path.toString());
                } else {
                    Path targetPath = organizedDir.resolve(path.getFileName());
                    Files.move(path, targetPath);
                    return targetPath.toString();
                }
            })
            .flatMap(List::stream);
    }
}
