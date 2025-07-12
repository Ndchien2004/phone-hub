package service;

import model.entity.Order;
import model.entity.Sales;

import java.util.List;
import java.util.Map;

public interface OrderAssignmentService {
    void processOrderAssignments();
    List<Order> getPendingOrders();
    List<Sales> getActiveSales();
    Map<Order, Sales> assignOrders(List<Order> orders, List<Sales> salesList);
    boolean updateOrderStatus(int orderId, String salesId);
    void sendAssignmentNotifications(Order order, Sales sales);
}
