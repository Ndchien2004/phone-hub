package service;

import model.entity.Order;

public interface EmailService {
    void sendOrderConfirmationEmail(Order order, String recipientEmail);
    boolean sendOrderCancellationEmail(Order order);
    boolean sendAddressChangeEmail(Order order, String oldAddress, String newAddress);
}