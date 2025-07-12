package proxy;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import config.CloudinaryConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Handles communication with Cloudinary for uploading images.
 */
public class CloudinaryProxy {

    private final Cloudinary cloudinary;

    public CloudinaryProxy() {
        // Initialize Cloudinary with configuration
        this.cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", CloudinaryConfig.CLOUD_NAME, "api_key", CloudinaryConfig.API_KEY, "api_secret", CloudinaryConfig.API_SECRET));
    }

    public String uploadAvatar(Part filePart, int userId) throws IOException {
        System.out.println("---------uploadAvatar");

        // Step 1: Create temporary file
        File tempFile = File.createTempFile("avatar-", ".tmp");
        try (InputStream input = filePart.getInputStream(); FileOutputStream output = new FileOutputStream(tempFile)) {
            input.transferTo(output);
        }

        // Step 2: Upload to folder "user-avatar/" with auto-generated name or userId
        Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                "folder", "user-avatar",                    // 👈 chỉ định folder
                "public_id", "user" + userId,               // 👈 tên file trong folder
                "overwrite", true
        ));

        // Step 3: Delete temp file
        tempFile.delete();

        // Step 4: Return secure_url
        return uploadResult.get("secure_url").toString();
    }

    public String uploadAvatar(Part filePart, int userId, HttpSession session, boolean hasExistingAvatar) throws IOException {
        File tempFile = File.createTempFile("avatar-", ".tmp");
        try (InputStream input = filePart.getInputStream(); FileOutputStream output = new FileOutputStream(tempFile)) {
            input.transferTo(output);
        }

        String publicId;

        if (!hasExistingAvatar) {
            // Lần đầu upload → dùng tên cố định để ghi đè
            publicId = "user" + userId;
        } else {
            // Upload tạm thời → đặt tên random
            publicId = "user" + userId + "-temp";

            // Ghi nhớ public_id để xóa nếu user không lưu
//            session.setAttribute("pendingAvatarPublicId", "user-avatar/" + publicId);
        }

        // Upload
        Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                "folder", "user-avatar",
                "public_id", publicId,
                "overwrite", true
        ));

        session.setAttribute("pendingAvatarUrl", uploadResult.get("secure_url"));
        System.out.println("pendingAvatarUrl: " + uploadResult.get("secure_url"));

//        session.setAttribute("pendingAvatarPublicId", uploadResult);
//        System.out.println("pendingAvatarPublicId: " + uploadResult);

        return (String) uploadResult.get("secure_url");
    }

    /**
     * Delete image on Cloudinary given a full image URL
     */
    public void deleteImage(String imageUrl) throws Exception {
        String publicId = extractPublicId(imageUrl);
        if (publicId == null) {
            throw new IllegalArgumentException("Cannot extract publicId from URL: " + imageUrl);
        }

        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        System.out.println("🗑️ Cloudinary delete result: " + result);
    }

    /**
     * Extract Cloudinary public_id from image URL
     */
    public String extractPublicId(String imageUrl) {
        try {
            // Ví dụ:
            // https://res.cloudinary.com/demo/image/upload/v1625238478/user-avatar/user123.jpg
            // => public_id = user-avatar/user123

            // Tách phần sau "upload/"
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            // Lấy phần sau upload/
            String afterUpload = imageUrl.substring(uploadIndex + "/upload/".length());

            // Bỏ version nếu có (bắt đầu bằng 'v' + số)
            String[] parts = afterUpload.split("/");
            int startIndex = 0;
            if (parts[0].matches("^v\\d+$")) {
                startIndex = 1;
            }

            // Ghép lại các phần còn lại, bỏ phần mở rộng (.jpg, .png, ...)
            StringBuilder publicId = new StringBuilder();
            for (int i = startIndex; i < parts.length; i++) {
                String part = parts[i];
                if (i == parts.length - 1 && part.contains(".")) {
                    part = part.substring(0, part.lastIndexOf('.'));
                }
                publicId.append(part);
                if (i < parts.length - 1) {
                    publicId.append("/");
                }
            }

            return publicId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String renameAvatarUrl(String pendingAvatarUrl, String originalAvatarUrl) {
        try {
            String tempPublicId = extractPublicId(pendingAvatarUrl);
            String originalPublicId = extractPublicId(originalAvatarUrl);

            if (tempPublicId == null || originalPublicId == null) {
                System.err.println("Failed to extract public_id from URL.");
                return "";
            }

            Map result = cloudinary.uploader().rename(
                    tempPublicId,
                    originalPublicId,
                    ObjectUtils.asMap("overwrite", true)
            );

            System.out.println("Renamed temporary image to official image: " + result);
            return result.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
