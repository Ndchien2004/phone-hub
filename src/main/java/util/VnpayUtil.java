package util;

import config.VnpayConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Lớp này chứa các hàm tiện ích được lấy trực tiếp từ code demo của VNPAY.
 * Việc sử dụng lại code này đảm bảo tính tương thích và chính xác 100%.
 */
public class VnpayUtil {

    public static String getPaymentUrl(Map<String, String> params, boolean encodeKey) {
        StringBuilder query = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = params.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && value.length() > 0) {
                try {
                    query.append(encodeKey ? URLEncoder.encode(key, StandardCharsets.US_ASCII.toString()) : key);
                    query.append('=');
                    query.append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                }
            }
        }
        return query.toString();
    }

    public static String hmacSHA512(String key, String data) {
        // Code hàm hmacSHA512 đã có trong VnpayConfig, chúng ta sẽ gọi nó
        return VnpayConfig.hmacSHA512(key, data);
    }
}