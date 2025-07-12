package service.assignment.impl;

import model.entity.Order;
import model.entity.Sales;
import service.assignment.OrderAssignmentAlgorithm;

import java.util.*;

public class RoundRobinAssignmentAlgorithm implements OrderAssignmentAlgorithm {
    @Override
    public Map<Order, Sales> assignOrders(List<Order> orders, List<Sales> salesList) {
        Map<Order, Sales> assignments = new HashMap<>();

        if (orders.isEmpty() || salesList.isEmpty()) {
            return assignments;
        }

        List<Sales> activeSales = salesList.stream()
                .filter(s -> "Active".equals(s.getStatus()) && !s.isIs_deleted())
                .sorted(Comparator.comparing(Sales::getCurrentAssignedOrders))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (activeSales.isEmpty()) {
            return assignments;
        }

        int salesIndex = 0;
        for (Order order : orders) {
            Sales assignedSales = activeSales.get(salesIndex);
            assignments.put(order, assignedSales);
            salesIndex = (salesIndex + 1) % activeSales.size();
        }

        return assignments;
    }

    @Override
    public String getAlgorithmName() {
        return "Round Robin";
    }
}
