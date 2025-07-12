package proxy;

import config.GoogleConfig;
import model.entity.User;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GoogleProxy {

    // Build Google OAuth2 authorization URL
    public String buildAuthorizationUrl() {
        return "https://accounts.google.com/o/oauth2/auth" +
                "?scope=email%20profile" +
                "&access_type=offline" +
                "&include_granted_scopes=true" +
                "&response_type=code" +
                "&redirect_uri=" + GoogleConfig.REDIRECT_URI +
                "&client_id=" + GoogleConfig.CLIENT_ID;
    }

    // Exchange code for access token and get user info
    public User getUserFromAuthorizationCode(String code) throws Exception {
        // Step 1: Request access token
        URL url = new URL(GoogleConfig.TOKEN_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        String data = "code=" + code +
                "&client_id=" + GoogleConfig.CLIENT_ID +
                "&client_secret=" + GoogleConfig.CLIENT_SECRET +
                "&redirect_uri=" + GoogleConfig.REDIRECT_URI +
                "&grant_type=authorization_code";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JSONObject tokenJson = new JSONObject(response.toString());
        String accessToken = tokenJson.getString("access_token");

        // Step 2: Get user info
        URL userInfoUrl = new URL(GoogleConfig.USERINFO_ENDPOINT + "?access_token=" + accessToken);
        HttpURLConnection userConn = (HttpURLConnection) userInfoUrl.openConnection();
        userConn.setRequestMethod("GET");

        BufferedReader userIn = new BufferedReader(new InputStreamReader(userConn.getInputStream()));
        StringBuilder userInfo = new StringBuilder();
        while ((line = userIn.readLine()) != null) {
            userInfo.append(line);
        }
        userIn.close();

        JSONObject userJson = new JSONObject(userInfo.toString());

        // Build User object
        User user = new User();
        user.setFullName(userJson.getString("name"));
        user.setEmail(userJson.getString("email"));
        user.setGoogleId(userJson.getString("id"));
        user.setAvatarUrl(userJson.getString("picture"));
        user.setDelete(false);
        return user;
    }
}
