package controller;

import config.VnpayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet("/vnpay-return")
public class VnpayReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--- [VnpayReturnServlet] Bắt đầu xử lý kết quả trả về ---");

        // Sử dụng TreeMap để các tham số được sắp xếp theo thứ tự alphabet
        Map<String, String> fields = new TreeMap<>();
        for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            // Quan trọng: Phải decode giá trị nhận về trước khi đưa vào map
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Lấy chữ ký từ VNPAY
        String vnp_SecureHash = req.getParameter("vnp_SecureHash");

        // Xóa chữ ký và loại chữ ký ra khỏi danh sách tham số để tạo lại hash
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // ==============================================================================
        // LOGIC TẠO LẠI HASH ĐỂ XÁC THỰC - PHẢI GIỐNG HỆT LÚC TẠO LINK
        // ==============================================================================
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            try {
                // Key và Value đều phải được encode
                hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
                hashData.append('=');
                hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Tạo lại chữ ký từ dữ liệu nhận được
        String mySecureHash = VnpayConfig.hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashData.toString());

        System.out.println("VNPAY Return - Chữ ký nhận được: " + vnp_SecureHash);
        System.out.println("VNPAY Return - Dữ liệu hash lại: " + hashData.toString());
        System.out.println("VNPAY Return - Chữ ký tự tạo:   " + mySecureHash);

        // So sánh hai chữ ký
        if (mySecureHash.equals(vnp_SecureHash)) {
            System.out.println("Chữ ký hợp lệ.");
            String vnp_ResponseCode = req.getParameter("vnp_ResponseCode");

            if ("00".equals(vnp_ResponseCode)) {
                System.out.println("Giao dịch VNPAY thành công.");
                req.setAttribute("message", "Thanh toán qua VNPAY thành công!");
                // Chuyển đến trang xác nhận, bạn có thể truyền thêm mã đơn hàng
                // req.setAttribute("orderId", req.getParameter("vnp_TxnRef"));
                req.getRequestDispatcher("/order-confirmation.jsp").forward(req, resp);
            } else {
                System.out.println("Giao dịch VNPAY thất bại. Mã lỗi: " + vnp_ResponseCode);
                req.setAttribute("errorMessage", "Thanh toán qua VNPAY thất bại. Mã lỗi: " + vnp_ResponseCode);
                req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
            }
        } else {
            // Chữ ký không hợp lệ
            System.err.println("Lỗi: Chữ ký VNPAY không hợp lệ!");
            req.setAttribute("errorMessage", "Giao dịch không hợp lệ do sai chữ ký.");
            req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
        }
    }

    /** Lấy tất cả tham số từ request và đưa vào Map */
    private Map<String, String> getAllRequestParams(HttpServletRequest req) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String param = paramNames.nextElement();
            String value = req.getParameter(param);
            if (value != null && !value.isEmpty()) {
                params.put(param, value);
            }
        }
        return params;
    }

    /** Tạo chuỗi hashData theo định dạng key=value&key=value... (sắp xếp theo key) */
    private String buildHashDataString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder hashData = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);
            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(key).append('=').append(value);
            }
        }
        return hashData.toString();
    }

    /** Mapping mã lỗi */
    private String getErrorMessage(String responseCode) {
        switch (responseCode) {
            case "07": return "Trừ tiền thành công. Giao dịch bị nghi ngờ.";
            case "09": return "Thẻ/Tài khoản chưa đăng ký InternetBanking.";
            case "10": return "Sai thông tin xác thực quá 3 lần.";
            case "11": return "Hết hạn chờ thanh toán.";
            case "12": return "Thẻ/Tài khoản bị khóa.";
            case "13": return "Sai mật khẩu xác thực (OTP).";
            case "24": return "Khách hàng hủy giao dịch.";
            case "51": return "Tài khoản không đủ số dư.";
            case "65": return "Vượt quá hạn mức giao dịch.";
            case "75": return "Ngân hàng đang bảo trì.";
            case "79": return "Sai mật khẩu thanh toán quá số lần.";
            default: return "Lỗi không xác định (Mã: " + responseCode + ")";
        }
    }
}
