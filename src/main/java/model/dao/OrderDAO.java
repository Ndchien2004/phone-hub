package model.dao;
import model.entity.Order;
import model.entity.OrderItem;

import java.util.Optional;

public interface OrderDAO {
    int saveOrder(Order order);
    void saveOrderItem(OrderItem item);
    Optional<Order> findByTransactionCode(String transactionCode);
    void updateOrderStatus(int orderId, int statusId);
    void updateOrderTransactionCode(int orderId, String transactionCode);
}