// 代码生成时间: 2025-10-05 23:48:41
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public class MediaAssetManagement {

    private static final Map<String, String> mediaAssets = new HashMap<>();
    private static final String MEDIA_ASSET_BASE_URL = "/media-assets";

    public static void main(String[] args) {
        Javalin app = Javalin.start(7000);
        app.routes(() -> {
            // Create a new media asset
            ApiBuilder.post(MEDIA_ASSET_BASE_URL + "/:id", MediaAssetManagement::createMediaAsset);
            // Get a media asset by ID
            ApiBuilder.get(MEDIA_ASSET_BASE_URL + "/:id", MediaAssetManagement::getMediaAsset);
            // Update a media asset by ID
            ApiBuilder.put(MEDIA_ASSET_BASE_URL + "/:id", MediaAssetManagement::updateMediaAsset);
            // Delete a media asset by ID
            ApiBuilder.delete(MEDIA_ASSET_BASE_URL + "/:id", MediaAssetManagement::deleteMediaAsset);
        });
    }

    // Create a new media asset
    private static void createMediaAsset(Context ctx) {
        String id = ctx.pathParam("id");
        String assetData = ctx.bodyAsClass(String.class);
        if (mediaAssets.containsKey(id)) {
            ctx.status(400).result("Media asset with ID 'id' already exists.");
            return;
        }
        mediaAssets.put(id, assetData);
        ctx.json(mediaAssets.get(id));
    }

    // Get a media asset by ID
    private static void getMediaAsset(Context ctx) {
        String id = ctx.pathParam("id");
        String assetData = mediaAssets.get(id);
        if (assetData == null) {
            ctx.status(404).result("Media asset with ID 'id' not found.");
            return;
        }
        ctx.json(assetData);
    }

    // Update a media asset by ID
    private static void updateMediaAsset(Context ctx) {
        String id = ctx.pathParam("id");
        String newAssetData = ctx.bodyAsClass(String.class);
        if (!mediaAssets.containsKey(id)) {
            ctx.status(404).result("Media asset with ID 'id' not found.");
            return;
        }
        mediaAssets.put(id, newAssetData);
        ctx.json(newAssetData);
    }

    // Delete a media asset by ID
    private static void deleteMediaAsset(Context ctx) {
        String id = ctx.pathParam("id");
        if (!mediaAssets.containsKey(id)) {
            ctx.status(404).result("Media asset with ID 'id' not found.");
            return;
        }
        mediaAssets.remove(id);
        ctx.status(204).result(null);
    }
}
