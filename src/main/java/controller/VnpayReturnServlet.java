package controller;

import config.VnpayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

@WebServlet("/vnpay-return")
public class VnpayReturnServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--- [VnpayReturnServlet] Bắt đầu xử lý kết quả trả về từ VNPAY ---");

        Map<String, String> fields = new TreeMap<>();
        for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Debug log
        System.out.println("Return - Tham số nhận được:");
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }

        // Lấy chữ ký từ VNPAY
        String vnp_SecureHash = req.getParameter("vnp_SecureHash");

        // Xóa chữ ký và loại chữ ký ra khỏi danh sách tham số để tạo lại hash
        Map<String, String> fieldsForHash = new TreeMap<>(fields);
        fieldsForHash.remove("vnp_SecureHashType");
        fieldsForHash.remove("vnp_SecureHash");

        // Sử dụng method buildHashData từ VnpayConfig để đảm bảo nhất quán
        String hashDataString = VnpayConfig.buildHashData(fieldsForHash);

        // Tạo lại chữ ký từ dữ liệu nhận được
        String mySecureHash = VnpayConfig.hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashDataString);

        System.out.println("Return - Hash data: " + hashDataString);
        System.out.println("Return - Chữ ký nhận được: " + vnp_SecureHash);
        System.out.println("Return - Chữ ký tự tạo: " + mySecureHash);

        // So sánh hai chữ ký
        if (mySecureHash.equals(vnp_SecureHash)) {
            System.out.println("Return - Chữ ký hợp lệ.");
            String vnp_ResponseCode = req.getParameter("vnp_ResponseCode");
            String vnp_TxnRef = req.getParameter("vnp_TxnRef");
            String vnp_Amount = req.getParameter("vnp_Amount");
            String vnp_BankCode = req.getParameter("vnp_BankCode");
            String vnp_PayDate = req.getParameter("vnp_PayDate");

            if ("00".equals(vnp_ResponseCode)) {
                // Giao dịch thành công
                System.out.println("Return - Giao dịch VNPAY thành công cho đơn hàng: " + vnp_TxnRef);

                // Truyền thông tin để hiển thị
                req.setAttribute("message", "Thanh toán qua VNPAY thành công!");
                req.setAttribute("transactionRef", vnp_TxnRef);
                req.setAttribute("paymentMethod", "VNPAY");
                req.setAttribute("amount", vnp_Amount);
                req.setAttribute("bankCode", vnp_BankCode);
                req.setAttribute("payDate", vnp_PayDate);

                req.getRequestDispatcher("/order-confirmation.jsp").forward(req, resp);
            } else {
                // Giao dịch thất bại
                System.out.println("Return - Giao dịch VNPAY thất bại cho đơn hàng: " + vnp_TxnRef + ". Mã lỗi: " + vnp_ResponseCode);

                // Mapping một số mã lỗi phổ biến
                String errorDetail = getErrorMessage(vnp_ResponseCode);

                req.setAttribute("errorMessage", "Thanh toán qua VNPAY thất bại: " + errorDetail);
                req.setAttribute("errorCode", vnp_ResponseCode);
                req.setAttribute("transactionRef", vnp_TxnRef);
                req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
            }
        } else {
            // Chữ ký không hợp lệ
            System.err.println("Return - Lỗi: Chữ ký VNPAY không hợp lệ!");
            req.setAttribute("errorMessage", "Giao dịch không hợp lệ do sai chữ ký.");
            req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
        }
    }

    // Thêm method để mapping mã lỗi VNPAY
    private String getErrorMessage(String responseCode) {
        switch (responseCode) {
            case "07":
                return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09":
                return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10":
                return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11":
                return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12":
                return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13":
                return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).";
            case "24":
                return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51":
                return "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
            case "65":
                return "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75":
                return "Ngân hàng thanh toán đang bảo trì.";
            case "79":
                return "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định.";
            default:
                return "Lỗi không xác định (Mã: " + responseCode + ")";
        }
    }
}