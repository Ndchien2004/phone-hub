package config;

public class CloudinaryConfig {
    private CloudinaryConfig() {
        // Private constructor to hide implicit public one
    }
    
    // Replace these with your actual Cloudinary credentials
    public static final String CLOUD_NAME = "phonehub";
    public static final String API_KEY = "741543149691946";
    public static final String API_SECRET = "qAPaF3KtXtQzpg-gWGN_0Uw-A-c";
    
    // Cloudinary upload settings
    public static final String UPLOAD_FOLDER = "phonehub/products";
    public static final boolean OVERWRITE = true;
    public static final String RESOURCE_TYPE = "image";
}
