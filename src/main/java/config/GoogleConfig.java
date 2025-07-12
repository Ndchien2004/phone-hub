package config;

public class GoogleConfig {
    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
    public static final String REDIRECT_URI = "http://localhost:9999/phone_hub_war/google-callback";
    public static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    public static final String USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v2/userinfo";
}

