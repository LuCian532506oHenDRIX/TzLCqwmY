// 代码生成时间: 2025-10-08 20:36:33
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import io.javalin.Javalin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebContentFetcher {

    // The main method to start the Javalin server.
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000); // Start Javalin on port 7000.

        // Define the route to fetch web content.
        app.get("/fetch/:url", ctx -> {
            String url = ctx.pathParam("url");
            try {
                String content = fetchWebContent(url);
                ctx.result(content); // Return the fetched content.
            } catch (IOException e) {
                ctx.result("Failed to fetch content: " + e.getMessage()); // Handle exceptions.
            }
        });
    }

    /**
     * Fetches web content from the specified URL.
     *
     * @param urlString The URL string to fetch content from.
     * @return The fetched content as a string.
     * @throws IOException If an I/O error occurs during content fetching.
     */
    private static String fetchWebContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("
");
            }
            return content.toString();
        } finally {
            conn.disconnect();
        }
    }
}
