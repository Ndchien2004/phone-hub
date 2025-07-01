package service.impl;

import config.PayOSConfig;
import model.dao.OrderDAO;
import model.dao.impl.OrderDAOImpl;
import model.entity.Cart;
import model.entity.CartItem;
import model.entity.Order;
import model.entity.OrderItem;
import service.OrderService;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    // CẬP NHẬT CÁC HẰNG SỐ CHO KHỚP VỚI DB
    private static final int STATUS_PENDING = 1;
    private static final int STATUS_PAID = 2;
    private static final int STATUS_PAYMENT_FAILED = 5;

    // ID mới cho phương thức thanh toán
    private static final int PM_COD = 10;
    private static final int PM_PAYOS = 11;

    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final PayOS payos = new PayOS(PayOSConfig.PAYOS_CLIENT_ID, PayOSConfig.PAYOS_API_KEY, PayOSConfig.PAYOS_CHECKSUM_KEY);

    @Override
    public Order createOrderFromGuestCart(Cart cart, String name, String email, String phone, String address, String note, String paymentMethod) {
        cart.calculateTotals();

        Order order = new Order();
        order.setUserId(null); // Guest order

        // SỬ DỤNG ID ĐÚNG DỰA TRÊN LỰA CHỌN CỦA NGƯỜI DÙNG
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentMethodId(PM_COD);
        } else {
            order.setPaymentMethodId(PM_PAYOS);
        }

        order.setStatusId(STATUS_PENDING);
        order.setFullName(name);
        order.setEmail(email);
        order.setPhoneNumber(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setTotalMoney(cart.getFinalTotal());

        int orderId = orderDAO.saveOrder(order);
        if (orderId == -1) {
            System.err.println("Lỗi OrderServiceImpl: Không thể lưu đơn hàng chính vào DB.");
            return null;
        }
        order.setOrderId(orderId);

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
            throw new IllegalArgumentException("Đối tượng Order không hợp lệ hoặc chưa được lưu.");
        }

        // Tạo mã giao dịch duy nhất
        long transactionCode = System.currentTimeMillis();

        // Chuẩn bị dữ liệu để gửi cho PayOS
        PaymentData paymentData = buildPaymentData(order, transactionCode, req);

        // Gọi API của PayOS
        CheckoutResponseData response = payos.createPaymentLink(paymentData);

        // Nếu tạo link thành công, cập nhật mã giao dịch vào đơn hàng trong DB
        if (response != null && response.getCheckoutUrl() != null && !response.getCheckoutUrl().isEmpty()) {
            orderDAO.updateOrderTransactionCode(order.getOrderId(), String.valueOf(transactionCode));
        } else {
            // Nếu không tạo được link, có thể throw exception để controller xử lý
            throw new RuntimeException("Tạo link thanh toán PayOS thất bại. Phản hồi từ PayOS không hợp lệ.");
        }

        return response;
    }

    /**
     * Phương thức trợ giúp để xây dựng đối tượng PaymentData, giúp code gọn gàng hơn.
     */
    private PaymentData buildPaymentData(Order order, long transactionCode, HttpServletRequest req) {
        int amount = (int) order.getTotalMoney();
        String description = "Thanh toan don hang #" + order.getOrderId();

        // Xây dựng URL trả về và hủy bỏ
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        String returnUrl = baseUrl + "/order-confirmation";
        String cancelUrl = baseUrl + "/checkout";

        // Chuyển đổi OrderItem thành ItemData mà PayOS yêu cầu
        List<ItemData> items = order.getOrderItems().stream()
                .map(orderItem -> ItemData.builder()
                        .name(orderItem.getProduct().getProductName())
                        .quantity(orderItem.getQuantity())
                        .price((int) orderItem.getPrice())
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
    public void processPaymentCallback(String transactionCode, String payosStatus) {
        orderDAO.findByTransactionCode(transactionCode).ifPresent(order -> {
            int newStatusId = "00".equals(payosStatus) ? STATUS_PAID : STATUS_PAYMENT_FAILED;

            // Chỉ cập nhật nếu trạng thái hiện tại là PENDING để tránh ghi đè
            if (order.getStatusId() == STATUS_PENDING) {
                orderDAO.updateOrderStatus(order.getOrderId(), newStatusId);
                System.out.println("Đã cập nhật trạng thái đơn hàng có mã giao dịch " + transactionCode + " thành " + newStatusId);
            } else {
                System.out.println("Bỏ qua cập nhật webhook cho đơn hàng " + transactionCode + " vì trạng thái không còn là PENDING.");
            }
        });
    }
}