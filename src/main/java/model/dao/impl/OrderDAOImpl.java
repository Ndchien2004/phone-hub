package model.dao.impl;

import model.dao.DBContext;
import model.dao.OrderDAO;
import model.entity.Order;
import model.entity.OrderItem;
import model.entity.Product;
import model.entity.Setting;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public int saveOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, payment_method_id, status_id, email, full_name, address, note, phone_number, total_money, discount, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

        // Sử dụng try-with-resources để đảm bảo kết nối được đóng
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        return -1;
    }//////////////////////////

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
    }///////////////////////

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
    }//////////////////////

    @Override
    public boolean updateOrderStatus(int orderId, int statusId) {
        String sql = "UPDATE orders SET status_id = ? WHERE order_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean cancelOrder(Integer orderId) {
        String sql = """
        UPDATE orders 
        SET status_id = (
            SELECT TOP 1 setting_id 
            FROM settings 
            WHERE setting_type = 'order_status' 
            AND (name = 'cancelled' OR description LIKE '%hủy%' OR description LIKE '%Hủy%')
        )
        WHERE order_id = ? 
        AND status_id IN (
            SELECT setting_id 
            FROM settings 
            WHERE setting_type = 'order_status' 
            AND (name IN ('pending', 'processing') 
                 OR description LIKE '%chờ%' 
                 OR description LIKE '%Chờ%'
                 OR description LIKE '%xử lý%'
                 OR description LIKE '%Xử lý%')
        )
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean canCancelOrder(Integer orderId) {
        String sql = """
        SELECT COUNT(*) as can_cancel
        FROM orders o
        LEFT JOIN settings s ON o.status_id = s.setting_id AND s.setting_type = 'order_status'
        WHERE o.order_id = ? 
        AND o.is_deleted = 0
        AND (s.name IN ('pending', 'processing') 
             OR s.description LIKE '%chờ%' 
             OR s.description LIKE '%Chờ%'
             OR s.description LIKE '%xử lý%'
             OR s.description LIKE '%Xử lý%')
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("can_cancel") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    ////////////////////////

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
    }//////////////////////

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, s.name as status_name, pm.name as payment_method_name FROM orders o " +
                "LEFT JOIN settings s ON o.status_id = s.setting_id " +
                "LEFT JOIN settings pm ON o.payment_method_id = pm.setting_id " +
                "WHERE o.is_deleted = 0 ORDER BY o.order_date DESC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs, false));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }////////////////////////

    @Override
    public Order getOrderById(Integer orderId) {
        String sql = "SELECT o.*, s.name as status_name, pm.name as payment_method_name FROM orders o " +
                "LEFT JOIN settings s ON o.status_id = s.setting_id " +
                "LEFT JOIN settings pm ON o.payment_method_id = pm.setting_id " +
                "WHERE o.order_id = ? AND o.is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs, true);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }///////////////////////
    ///
    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.*, p.product_name, p.image as imageUrl, p.color, p.memory, p.brief_info, p.price_sale, p.price_origin " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ? AND oi.is_deleted = 0";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getLong("price"));

                    Product product = new Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setImageUrl(rs.getString("imageUrl"));
                    product.setColor(rs.getString("color"));
                    product.setMemory(rs.getString("memory"));
                    product.setBriefInfo(rs.getString("brief_info"));
                    product.setPriceSale(rs.getLong("price_sale"));
                    product.setPriceOrigin(rs.getLong("price_origin"));

                    item.setProduct(product);
                    orderItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }/////////////////////////

    // --- PHƯƠNG THỨC TRỢ GIÚP ---
    private Order mapResultSetToOrder(ResultSet rs, boolean loadItems) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId((Integer) rs.getObject("user_id"));
        order.setStatusId(rs.getInt("status_id"));
        order.setPaymentMethodId(rs.getInt("payment_method_id"));
        order.setEmail(rs.getString("email"));
        order.setFullName(rs.getString("full_name"));
        order.setAddress(rs.getString("address"));
        order.setNote(rs.getString("note"));
        order.setPhoneNumber(rs.getString("phone_number"));
        order.setTotalMoney(rs.getLong("total_money"));
        if (rs.getTimestamp("order_date") != null) {
            order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        }
        order.setDiscount((Integer) rs.getObject("discount"));
        order.setTransactionCode(rs.getString("transaction_code"));

        Setting status = new Setting();
        status.setSettingId(rs.getInt("status_id"));
        status.setName(rs.getString("status_name"));
        order.setStatus(status);

        Setting paymentMethod = new Setting();
        paymentMethod.setSettingId(rs.getInt("payment_method_id"));
        paymentMethod.setName(rs.getString("payment_method_name"));
        order.setPaymentMethod(paymentMethod);

        if (loadItems) {
            order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
        }

        return order;
    }
}