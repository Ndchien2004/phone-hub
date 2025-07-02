package service.impl;

import config.PayOSConfig;
import config.VnpayConfig;
import model.dao.DBContext;
import model.dao.OrderDAO;
import model.dao.impl.OrderDAOImpl;
import model.entity.*;
import service.OrderService;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import jakarta.servlet.http.HttpServletRequest;
import config.VnpayConfig;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.TreeMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    // --- Hằng số để quản lý trạng thái và phương thức thanh toán ---
    private static final int STATUS_PENDING = 1;
    private static final int STATUS_PAID = 2;
    private static final int STATUS_PAYMENT_FAILED = 5;

    private static final int PM_COD = 10;
    private static final int PM_PAYOS = 11;
    private static final int PM_VNPAY = 12;

    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final PayOS payos = new PayOS(PayOSConfig.PAYOS_CLIENT_ID, PayOSConfig.PAYOS_API_KEY, PayOSConfig.PAYOS_CHECKSUM_KEY);

    @Override
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderDAO.getOrderById(orderId);
    }

    @Override
    public boolean updateOrderStatus(int orderId, int statusId) {
        return orderDAO.updateOrderStatus(orderId, statusId);
    }

    // Các method mới cho VNPAY
    @Override
    public Optional<Order> findByTransactionCode(String transactionCode) {
        return orderDAO.findByTransactionCode(transactionCode);
    }

    @Override
    public boolean validateOrderAmount(String transactionCode, long vnpayAmount) {
        try {
            Optional<Order> orderOpt = orderDAO.findByTransactionCode(transactionCode);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // VNPAY amount đã nhân 100, nên cần nhân total với 100 để so sánh
                long expectedAmount = Math.round(order.getTotalMoney() * 100);
                boolean isValid = expectedAmount == vnpayAmount;

                System.out.println("Kiểm tra số tiền - Order: " + expectedAmount + ", VNPAY: " + vnpayAmount + ", Valid: " + isValid);
                return isValid;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra số tiền: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Order createOrderFromGuestCart(Cart cart, String name, String email, String phone, String address, String note, String paymentMethod) {
        cart.calculateTotals();

        Order order = new Order();
        order.setUserId(null); // Guest order

        // Gán PaymentMethodId và StatusId một cách linh hoạt
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentMethodId(PM_COD);
        } else if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentMethodId(PM_VNPAY);
        } else if ("PayOS".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentMethodId(PM_PAYOS);
        } else {
            // Mặc định hoặc xử lý lỗi nếu không có phương thức thanh toán hợp lệ
            // Ở đây ta tạm thời mặc định là COD
            order.setPaymentMethodId(PM_COD);
        }
        order.setStatusId(STATUS_PENDING);

        // Điền các thông tin khác
        order.setFullName(name);
        order.setEmail(email);
        order.setPhoneNumber(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setTotalMoney(cart.getFinalTotal());

        // Lưu đơn hàng chính để lấy ID
        int orderId = orderDAO.saveOrder(order);
        if (orderId == -1) {
            System.err.println("Lỗi OrderServiceImpl: Không thể lưu đơn hàng vào DB.");
            return null;
        }
        order.setOrderId(orderId);

        // Lưu các mục hàng chi tiết
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderDAO.saveOrderItem(orderItem);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        return order;
    }

    @Override
    public CheckoutResponseData createPaymentLink(Order order, HttpServletRequest req) throws Exception {
        if (order == null || order.getOrderId() == null) {
            throw new IllegalArgumentException("Đối tượng Order không hợp lệ.");
        }

        long transactionCode = System.currentTimeMillis();
        PaymentData paymentData = buildPayOSPaymentData(order, transactionCode, req);
        CheckoutResponseData response = payos.createPaymentLink(paymentData);

        if (response != null && response.getCheckoutUrl() != null && !response.getCheckoutUrl().isEmpty()) {
            orderDAO.updateOrderTransactionCode(order.getOrderId(), String.valueOf(transactionCode));
        } else {
            throw new RuntimeException("Tạo link thanh toán PayOS thất bại.");
        }
        return response;
    }

    private PaymentData buildPayOSPaymentData(Order order, long transactionCode, HttpServletRequest req) {
        int amount = (int) order.getTotalMoney();
        String description = "Thanh toan don hang #" + order.getOrderId();
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        String returnUrl = baseUrl + "/order-confirmation";
        String cancelUrl = baseUrl + "/checkout";

        List<ItemData> items = order.getOrderItems().stream()
                .map(item -> ItemData.builder()
                        .name(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .price((int) item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return PaymentData.builder()
                .orderCode(transactionCode)
                .amount(amount)
                .description(description)
                .items(items)
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .buyerName(order.getFullName())
                .buyerEmail(order.getEmail())
                .buyerPhone(order.getPhoneNumber())
                .buyerAddress(order.getAddress())
                .build();
    }


    @Override
    public String createVnpayPaymentUrl(Order order, HttpServletRequest req) {
        long amount = order.getTotalMoney() * 100;
        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        String vnp_IpAddr = getClientIP(req);

        // Sử dụng TreeMap để các tham số được tự động sắp xếp theo alphabet
        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", VnpayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", VnpayConfig.VNP_COMMAND_PAY);
        vnp_Params.put("vnp_TmnCode", VnpayConfig.VNP_TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang #" + order.getOrderId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");

        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        vnp_Params.put("vnp_ReturnUrl", baseUrl + "/vnpay-return");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // ==============================================================================
        // LOGIC TẠO HASH VÀ URL ĐƯỢC SỬA LẠI - QUAN TRỌNG!
        // ==============================================================================

        // Bước 1: Tạo chuỗi dữ liệu để băm (hashData)
        StringBuilder hashData = new StringBuilder();

        // SỬA: Sử dụng trực tiếp entrySet() của TreeMap để đảm bảo thứ tự
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (fieldValue != null && fieldValue.length() > 0) {
                if (!isFirst) {
                    hashData.append('&');
                }

                try {
                    hashData.append(fieldName);
                    hashData.append('=');
                    // QUAN TRỌNG: Encode giá trị ở đây
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    isFirst = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String hashDataString = hashData.toString();

        // Bước 2: Tạo chữ ký từ chuỗi hashData
        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashDataString);

        // Bước 3: Tạo URL cuối cùng để gửi đi
        String queryUrl = hashDataString + "&vnp_SecureHash=" + vnp_SecureHash;
        String finalUrl = VnpayConfig.VNP_PAY_URL + "?" + queryUrl;

        // Cập nhật mã giao dịch vào DB
        orderDAO.updateOrderTransactionCode(order.getOrderId(), vnp_TxnRef);

        // Debug log
        System.out.println("================ VNPAY DEBUG (FIXED VERSION) ================");
        System.out.println("Số lượng tham số: " + vnp_Params.size());
        System.out.println("Thứ tự tham số:");
        for (String key : vnp_Params.keySet()) {
            System.out.println("  " + key + " = " + vnp_Params.get(key));
        }
        System.out.println("Dữ liệu gốc để hash: " + hashDataString);
        System.out.println("Chữ ký được tạo: " + vnp_SecureHash);
        System.out.println("URL cuối cùng: " + finalUrl);
        System.out.println("==========================================================");

        return finalUrl;
    }

    @Override
    public void processVnpayCallback(String transactionCode, String paymentStatus) {
        orderDAO.findByTransactionCode(transactionCode).ifPresent(order -> {
            int newStatusId;

            // Xử lý mã trạng thái từ cả PayOS và VNPAY
            if ("00".equals(paymentStatus) || "PAID".equals(paymentStatus)) {
                newStatusId = STATUS_PAID;
                System.out.println("Giao dịch thành công cho đơn hàng: " + transactionCode);
            } else {
                newStatusId = STATUS_PAYMENT_FAILED;
                System.out.println("Giao dịch thất bại cho đơn hàng: " + transactionCode + ", mã lỗi: " + paymentStatus);
            }

            // Chỉ cập nhật nếu đơn hàng đang ở trạng thái pending
            if (order.getStatusId() == STATUS_PENDING) {
                boolean updated = orderDAO.updateOrderStatus(order.getOrderId(), newStatusId);
                if (updated) {
                    System.out.println("Đã cập nhật trạng thái đơn hàng " + transactionCode + " thành " + newStatusId);
                } else {
                    System.err.println("Lỗi khi cập nhật trạng thái đơn hàng " + transactionCode);
                }
            } else {
                System.out.println("Đơn hàng " + transactionCode + " đã được xử lý trước đó (status: " + order.getStatusId() + "), bỏ qua cập nhật");
            }
        });
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    // --- LOGIC XỬ LÝ CALLBACK ---
    @Override
    public void processPaymentCallback(String transactionCode, String payosStatus) {
        orderDAO.findByTransactionCode(transactionCode).ifPresent(order -> {
            int newStatusId = "00".equals(payosStatus) ? STATUS_PAID : STATUS_PAYMENT_FAILED;
            if (order.getStatusId() == STATUS_PENDING) {
                orderDAO.updateOrderStatus(order.getOrderId(), newStatusId);
                System.out.println("Đã cập nhật trạng thái đơn hàng (PayOS) " + transactionCode + " thành " + newStatusId);
            }
        });
    }
}