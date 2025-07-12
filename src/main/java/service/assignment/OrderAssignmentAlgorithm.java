package service.assignment;

import model.entity.Order;
import model.entity.Sales;

import java.util.List;
import java.util.Map;

public interface OrderAssignmentAlgorithm {
    Map<Order, Sales> assignOrders(List<Order> orders, List<Sales> salesList);
    String getAlgorithmName();
}
