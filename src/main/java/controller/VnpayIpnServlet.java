package controller;

import config.VnpayConfig;
import service.OrderService;
import service.impl.OrderServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import com.google.gson.Gson;

@WebServlet("/vnpay-ipn")
public class VnpayIpnServlet extends HttpServlet {
    private final OrderService orderService = new OrderServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("--- [VnpayIpnServlet] Bắt đầu xử lý IPN từ VNPAY ---");

        String RspCode;
        String Message;
        try {
            // Sử dụng TreeMap để đảm bảo thứ tự sắp xếp
            Map<String, String> fields = new TreeMap<>();

            for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = params.nextElement();
                String fieldValue = req.getParameter(fieldName);

                if (fieldValue != null && fieldValue.length() > 0) {
                    fields.put(fieldName, fieldValue);
                }
            }

            // Debug log
            System.out.println("IPN - Tham số nhận được:");
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }

            String vnp_SecureHash = req.getParameter("vnp_SecureHash");

            // Xóa các trường hash để tạo lại signature
            Map<String, String> fieldsForHash = new TreeMap<>(fields);
            fieldsForHash.remove("vnp_SecureHashType");
            fieldsForHash.remove("vnp_SecureHash");

            // Sử dụng method buildHashData từ VnpayConfig
            String hashDataString = VnpayConfig.buildHashData(fieldsForHash);
            String mySecureHash = VnpayConfig.hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashDataString);

            System.out.println("IPN - Hash data: " + hashDataString);
            System.out.println("IPN - Chữ ký nhận được: " + vnp_SecureHash);
            System.out.println("IPN - Chữ ký tự tạo: " + mySecureHash);

            if (mySecureHash.equals(vnp_SecureHash)) {
                System.out.println("IPN - Chữ ký hợp lệ");

                String vnp_TxnRef = req.getParameter("vnp_TxnRef");
                String vnp_ResponseCode = req.getParameter("vnp_ResponseCode");
                String vnp_Amount = req.getParameter("vnp_Amount");

                // Kiểm tra đơn hàng tồn tại
                boolean checkOrder = orderService.findByTransactionCode(vnp_TxnRef).isPresent();
                boolean checkAmount = true;

                if (vnp_Amount != null && checkOrder) {
                    try {
                        long vnpayAmount = Long.parseLong(vnp_Amount); // VNPAY amount đã nhân 100
                        checkAmount = orderService.validateOrderAmount(vnp_TxnRef, vnpayAmount);
                    } catch (NumberFormatException e) {
                        System.err.println("IPN - Lỗi parse amount: " + e.getMessage());
                        checkAmount = false;
                    }
                }

                if (checkOrder) {
                    if (checkAmount) {
                        // Xử lý callback với mã trạng thái VNPAY
                        orderService.processVnpayCallback(vnp_TxnRef, vnp_ResponseCode);

                        System.out.println("IPN - Xử lý thành công cho đơn hàng " + vnp_TxnRef);
                        RspCode = "00";
                        Message = "Confirm Success";
                    } else {
                        System.err.println("IPN - Sai số tiền cho đơn hàng " + vnp_TxnRef);
                        RspCode = "04";
                        Message = "Invalid Amount";
                    }
                } else {
                    System.err.println("IPN - Không tìm thấy đơn hàng " + vnp_TxnRef);
                    RspCode = "01";
                    Message = "Order not Found";
                }
            } else {
                System.err.println("IPN - Chữ ký không hợp lệ!");
                RspCode = "97";
                Message = "Invalid Checksum";
            }
        } catch (Exception e) {
            System.err.println("IPN - Lỗi xử lý: " + e.getMessage());
            e.printStackTrace();
            RspCode = "99";
            Message = "Unknown error";
        }

        // Trả về response cho VNPAY
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String jsonResponse = gson.toJson(Map.of("RspCode", RspCode, "Message", Message));
        System.out.println("IPN - Response: " + jsonResponse);
        resp.getWriter().write(jsonResponse);
    }
}