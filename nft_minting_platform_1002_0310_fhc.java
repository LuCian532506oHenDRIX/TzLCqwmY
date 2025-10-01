// 代码生成时间: 2025-10-02 03:10:28
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.plugin.json.JavalinJackson;
import java.util.HashMap;
import java.util.Map;

public class NftMintingPlatform {

    private static final String BASE_URL = "/api/nft";
    private static final String CREATE_ENDPOINT = BASE_URL + "/create";
    private static final String FETCH_ENDPOINT = BASE_URL + "/:id";
    private static final String UPDATE_ENDPOINT = BASE_URL + "/update/:id";
    private static final String DELETE_ENDPOINT = BASE_URL + "/delete/:id";

    // In-memory storage for NFTs
    private static final Map<String, Nft> nftStorage = new HashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create()
            .registerPlugin(new JavalinJackson())
            .start(7000); // Start the server on port 7000

        // Define routes
        app.routes(() -> {

            // Create an NFT
            ApiBuilder.post(CREATE_ENDPOINT, NftMintingPlatform::createNft);

            // Fetch an NFT by ID
            ApiBuilder.get(FETCH_ENDPOINT, NftMintingPlatform::fetchNft);

            // Update an NFT by ID
            ApiBuilder.put(UPDATE_ENDPOINT, NftMintingPlatform::updateNft);

            // Delete an NFT by ID
            ApiBuilder.delete(DELETE_ENDPOINT, NftMintingPlatform::deleteNft);
        });
    }

    // NFT data model
    public static class Nft {
        private String id;
        private String name;
        private String description;
        private String imageUrl;

        public Nft(String id, String name, String description, String imageUrl) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    // Endpoint handlers
    private static void createNft(Context ctx) {
        // Parse request body to Nft object
        Nft nft = ctx.bodyAsClass(Nft.class);
        if (nft == null) {
            ctx.status(400).result("Invalid NFT data");
            return;
        }
        nftStorage.put(nft.getId(), nft);
        ctx.status(201).result(nft);
    }

    private static void fetchNft(Context ctx) {
        String nftId = ctx.pathParam("id");
        Nft nft = nftStorage.get(nftId);
        if (nft == null) {
            ctx.status(404).result("NFT not found");
            return;
        }
        ctx.json(nft);
    }

    private static void updateNft(Context ctx) {
        String nftId = ctx.pathParam("id");
        Nft existingNft = nftStorage.get(nftId);
        if (existingNft == null) {
            ctx.status(404).result("NFT not found");
            return;
        }
        Nft updatedNft = ctx.bodyAsClass(Nft.class);
        if (updatedNft == null) {
            ctx.status(400).result("Invalid NFT data");
            return;
        }
        // Update the NFT with new data
        existingNft.setName(updatedNft.getName());
        existingNft.setDescription(updatedNft.getDescription());
        existingNft.setImageUrl(updatedNft.getImageUrl());
        nftStorage.put(nftId, existingNft);
        ctx.json(existingNft);
    }

    private static void deleteNft(Context ctx) {
        String nftId = ctx.pathParam("id");
        if (!nftStorage.containsKey(nftId)) {
            ctx.status(404).result("NFT not found");
            return;
        }
        nftStorage.remove(nftId);
        ctx.status(204).result(null);
    }
}
