// 代码生成时间: 2025-09-20 07:40:32
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder.*;
import java.util.List;
import java.util.stream.Collectors;

public class ImageResizer {
    // Define the port on which the Javalin server will run
    private static final int PORT = 7000;
    // Define the maximum allowed size in bytes for an uploaded file
    private static final long MAX_UPLOAD_SIZE = 10 * 1024 * 1024; // 10 MB

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false; // Disable Javalin banner
            config.uploadSizeLimit = MAX_UPLOAD_SIZE; // Set the upload size limit
        })
            .start(PORT);

        app.post("/resize", ctx -> {
            // Retrieve the uploaded file from the request
            File uploadedFile = ctx.uploadedFile("image");
            if (uploadedFile == null) {
                ctx.status(400).result("No image file uploaded.");
                return;
            }

            try {
                // Read the image file into a BufferedImage
                BufferedImage image = ImageIO.read(uploadedFile);
                if (image == null) {
                    ctx.status(400).result("Failed to read the uploaded image.");
                    return;
                }

                // Define the new dimensions for the resized image
                int newWidth = 800; // New width in pixels
                int newHeight = 600; // New height in pixels

                // Resize the image using a placeholder method
                BufferedImage resizedImage = resizeImage(image, newWidth, newHeight);

                // Write the resized image to a new file
                File resizedFile = new File(uploadedFile.getAbsolutePath() + "_resized.jpg");
                ImageIO.write(resizedImage, "jpg", resizedFile);

                // Return the path to the resized image file
                ctx.result("Resized image path: " + resizedFile.getAbsolutePath());
            } catch (Exception e) {
                // Handle any exceptions that occur during the image resizing process
                ctx.status(500).result("Error resizing image: " + e.getMessage());
            }
        });
    }

    // Placeholder method for resizing an image
    // This method should be replaced with actual resizing logic
    private static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        // Create a new BufferedImage with the new dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // Draw the original image onto the new BufferedImage, scaling it to fit the new dimensions
        resizedImage.getGraphics().drawImage(
            originalImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH),
            0, 0, null
        );

        // Return the resized image
        return resizedImage;
    }
}
