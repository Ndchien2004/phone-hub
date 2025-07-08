package model.dao;
import model.entity.Order;
import model.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    int saveOrder(Order order);
    void saveOrderItem(OrderItem item);
    Optional<Order> findByTransactionCode(String transactionCode);
    void updateOrderTransactionCode(int orderId, String transactionCode);
    List<Order> getAllOrders();
    Order getOrderById(Integer orderId);
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);
    boolean updateOrderStatus(int orderId, int statusId);
    boolean cancelOrder(Integer orderId);
    boolean canCancelOrder(Integer orderId);
}