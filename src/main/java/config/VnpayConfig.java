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
    public static final String VNP_TMN_CODE = "7W96V6LJ";
    public static final String VNP_HASH_SECRET = "LK3VBYZ7Y7TL3BUEW5HSJON9V2M6K6CR";
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException("Key or data is null");
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
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

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
                    // QUAN TRỌNG: Phải encode giá trị
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