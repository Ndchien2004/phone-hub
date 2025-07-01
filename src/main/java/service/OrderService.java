package service;

import model.entity.Cart;
import model.entity.Order;
import vn.payos.type.CheckoutResponseData;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    Order createOrderFromGuestCart(Cart cart, String name, String email, String phone, String address, String note, String paymentMethod);
    CheckoutResponseData createPaymentLink(Order order, HttpServletRequest req) throws Exception;
    void processPaymentCallback(String transactionCode, String payosStatus);
}