package config;

public class CloudinaryConfig {
    private CloudinaryConfig() {
        // Private constructor to hide implicit public one
    }
    
    // Replace these with your actual Cloudinary credentials
    public static final String CLOUD_NAME = "ddka14f5u";
    public static final String API_KEY = "525549366334319";
    public static final String API_SECRET = "hIN9A3iMbZqytgRyUq2TbYvBf6s";
    
    // Cloudinary upload settings
    public static final String UPLOAD_FOLDER = "phonehub/products";
    public static final boolean OVERWRITE = true;
    public static final String RESOURCE_TYPE = "image";
}
