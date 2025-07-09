package service;

import model.entity.Order;

public interface EmailService {
    void sendOrderConfirmationEmail(Order order, String recipientEmail);
}