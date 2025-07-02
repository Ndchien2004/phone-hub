package config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class VnpayConfig {

    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND_PAY = "pay";
    public static final String VNP_TMN_CODE = "O9RMI6HK";
    public static final String VNP_HASH_SECRET = "Z8GU35RTZPG3SOYG4L4XP7V527LX5EVA";
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // Tiện ích băm dữ liệu theo thuật toán HmacSHA512
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // Tiện ích lấy IP của khách hàng
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAddress = "Invalid IP:" + e.getMessage();
        }
        return ipAddress;
    }

    // Tiện ích tạo mã tham chiếu ngẫu nhiên
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Tạo hash cho tất cả các field
    public static String hashAllFields(Map<String, String> fields) {
        String hashData = buildHashData(fields);
        return hmacSHA512(VNP_HASH_SECRET, hashData);
    }

    // Tạo hash data string (không bao gồm việc hash)
    public static String buildHashData(Map<String, String> fields) {
        Map<String, String> sortedFields = new TreeMap<>(fields);

        StringBuilder hashData = new StringBuilder();
        boolean isFirst = true;

        for (Map.Entry<String, String> entry : sortedFields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (fieldValue != null && fieldValue.length() > 0) {
                if (!isFirst) {
                    hashData.append('&');
                }

                try {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    isFirst = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return hashData.toString();
    }
}