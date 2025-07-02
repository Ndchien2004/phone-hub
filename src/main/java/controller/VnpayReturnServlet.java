package controller;

import config.VnpayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/vnpay-return")
public class VnpayReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--- [VnpayReturnServlet] Bắt đầu xử lý kết quả trả về từ VNPAY ---");

        // Lấy toàn bộ tham số từ request
        Map<String, String> vnp_Params = getAllRequestParams(req);

        // Lấy và loại bỏ chữ ký
        String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHashType");

        // Tạo chuỗi dữ liệu để hash lại
        String hashData = buildHashDataString(vnp_Params);
        String generatedHash = VnpayConfig.hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashData);

        // In log debug
        System.out.println("Hash Data String: " + hashData);
        System.out.println("SecureHash (received): " + vnp_SecureHash);
        System.out.println("SecureHash (generated): " + generatedHash);

        if (generatedHash.equals(vnp_SecureHash)) {
            System.out.println("✅ Chữ ký hợp lệ.");

            String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");
            String vnp_TxnRef = vnp_Params.get("vnp_TxnRef");
            String vnp_Amount = vnp_Params.get("vnp_Amount");
            String vnp_BankCode = vnp_Params.get("vnp_BankCode");
            String vnp_PayDate = vnp_Params.get("vnp_PayDate");

            if ("00".equals(vnp_ResponseCode)) {
                // Giao dịch thành công
                System.out.println("✅ Giao dịch thành công cho đơn hàng: " + vnp_TxnRef);

                req.setAttribute("message", "Thanh toán qua VNPAY thành công!");
                req.setAttribute("transactionRef", vnp_TxnRef);
                req.setAttribute("paymentMethod", "VNPAY");
                req.setAttribute("amount", vnp_Amount);
                req.setAttribute("bankCode", vnp_BankCode);
                req.setAttribute("payDate", vnp_PayDate);

                req.getRequestDispatcher("/order-confirmation.jsp").forward(req, resp);
            } else {
                // Giao dịch thất bại
                System.out.println("❌ Giao dịch thất bại. Mã lỗi: " + vnp_ResponseCode);

                String errorMessage = getErrorMessage(vnp_ResponseCode);
                req.setAttribute("errorMessage", "Thanh toán qua VNPAY thất bại: " + errorMessage);
                req.setAttribute("errorCode", vnp_ResponseCode);
                req.setAttribute("transactionRef", vnp_TxnRef);
                req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
            }

        } else {
            // Chữ ký không hợp lệ
            System.err.println("❌ Chữ ký không hợp lệ!");
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
