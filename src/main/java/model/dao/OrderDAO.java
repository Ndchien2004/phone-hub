package model.dao;

import model.entity.Order;
import model.entity.OrderItem;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.order_id, o.email, o.full_name, o.address, o.note, 
                   o.phone_number, o.total_money, o.order_date, o.discount,
                   o.is_deleted
            FROM orders o
            WHERE o.is_deleted = 0
            ORDER BY o.order_date DESC
            """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setEmail(rs.getString("email"));
                order.setFullName(rs.getString("full_name"));
                order.setAddress(rs.getString("address"));
                order.setNote(rs.getString("note"));
                order.setPhoneNumber(rs.getString("phone_number"));
                order.setTotalMoney(rs.getLong("total_money"));

                Timestamp timestamp = rs.getTimestamp("order_date");
                if (timestamp != null) {
                    order.setOrderDate(timestamp.toLocalDateTime());
                }

                order.setDiscount(rs.getInt("discount"));
                order.setDeleted(rs.getBoolean("is_deleted"));

                // Load order items for this order
                order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = """
            SELECT oi.order_item_id, oi.quantity, oi.price, 
                   p.product_name, p.image_url
            FROM order_items oi
            LEFT JOIN products p ON oi.product_id = p.product_id
            WHERE oi.order_id = ?
            """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderItemId(rs.getInt("order_item_id"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setPrice(rs.getLong("price"));

                    // Set product name temporarily (we'll need to create a simple product object or use a DTO)
                    orderItem.setProductName(rs.getString("product_name"));
                    orderItem.setProductImageUrl(rs.getString("image_url"));

                    orderItems.add(orderItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    public Order getOrderById(Integer orderId) {
        Order order = null;
        String sql = """
        SELECT o.order_id, o.email, o.full_name, o.address, o.note, 
               o.phone_number, o.total_money, o.order_date, o.discount,
               o.is_deleted
        FROM orders o
        WHERE o.order_id = ? AND o.is_deleted = 0
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setEmail(rs.getString("email"));
                    order.setFullName(rs.getString("full_name"));
                    order.setAddress(rs.getString("address"));
                    order.setNote(rs.getString("note"));
                    order.setPhoneNumber(rs.getString("phone_number"));
                    order.setTotalMoney(rs.getLong("total_money"));

                    Timestamp timestamp = rs.getTimestamp("order_date");
                    if (timestamp != null) {
                        order.setOrderDate(timestamp.toLocalDateTime());
                    }

                    order.setDiscount(rs.getInt("discount"));
                    order.setDeleted(rs.getBoolean("is_deleted"));

                    // Load order items for this order
                    order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }
}