package service;

import model.entity.Cart;
import model.entity.Order;
import vn.payos.type.CheckoutResponseData;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(int orderId);
    boolean updateOrderStatus(int orderId, int statusId);

    //hỗ trợ luồng thanh toán
    Order createOrderFromGuestCart(Cart cart, String name, String email, String phone, String address, String note, String paymentMethod);
    CheckoutResponseData createPaymentLink(Order order, HttpServletRequest req) throws Exception;
    String createVnpayPaymentUrl(Order order, HttpServletRequest req);

    //callback từ cổng thanh toán
    void processPaymentCallback(String transactionCode, String paymentStatus);
    void processVnpayCallback(String transactionCode, String paymentStatus);
    boolean validateOrderAmount(String transactionCode, long vnpayAmount);
    Optional<Order> findByTransactionCode(String transactionCode);
}