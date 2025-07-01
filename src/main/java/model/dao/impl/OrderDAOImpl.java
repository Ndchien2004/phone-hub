package model.dao.impl;

import model.dao.DBContext;
import model.dao.OrderDAO;
import model.entity.Order;
import model.entity.OrderItem;
import java.sql.*;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    // Trong file model/dao/impl/OrderDAOImpl.java

    @Override
    public int saveOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, payment_method_id, status_id, email, full_name, address, note, phone_number, total_money, discount, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

        // Sử dụng try-with-resources để đảm bảo kết nối được đóng
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Xử lý các giá trị có thể là NULL một cách an toàn
            // Dùng setObject là cách tốt nhất vì nó có thể xử lý cả giá trị và null
            ps.setObject(1, order.getUserId(), Types.INTEGER);
            ps.setObject(2, order.getPaymentMethodId(), Types.INTEGER);
            ps.setObject(3, order.getStatusId(), Types.INTEGER);

            ps.setString(4, order.getEmail());
            ps.setString(5, order.getFullName());
            ps.setString(6, order.getAddress());
            ps.setString(7, order.getNote());
            ps.setString(8, order.getPhoneNumber());
            ps.setLong(9, order.getTotalMoney());

            // Xử lý giá trị NULL cho discount
            ps.setObject(10, order.getDiscount(), Types.INTEGER);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        // Trả về ID của đơn hàng vừa được tạo
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            // In lỗi ra console để dễ dàng debug
            System.err.println("SQLException trong saveOrder: " + e.getMessage());
            e.printStackTrace();
        }

        // Trả về -1 nếu có lỗi xảy ra
        return -1;
    }

    @Override
    public void saveOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price, is_deleted) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getOrderId());
            // Lấy productId từ đối tượng Product bên trong OrderItem
            ps.setInt(2, item.getProduct().getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setLong(4, item.getPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Order> findByTransactionCode(String transactionCode) {
        String sql = "SELECT * FROM orders WHERE transaction_code = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setStatusId(rs.getInt("status_id"));
                    // Populate other fields if needed
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void updateOrderStatus(int orderId, int statusId) {
        String sql = "UPDATE orders SET status_id = ? WHERE order_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderTransactionCode(int orderId, String transactionCode) {
        String sql = "UPDATE orders SET transaction_code = ? WHERE order_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionCode);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}