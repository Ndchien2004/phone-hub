package model.dao;

import model.entity.Order;
import model.entity.OrderItem;
import model.entity.Product;
import model.entity.Setting;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
        SELECT o.order_id, o.user_id, o.email, o.full_name, o.address, 
               o.note, o.phone_number, o.total_money, o.order_date, o.discount, 
               s.setting_id, s.name as status_name, s.description as status_description,
               pm.setting_id as payment_method_id, pm.name as payment_method_name, pm.description as payment_method_description
        FROM orders o
        LEFT JOIN settings s ON o.status_id = s.setting_id AND s.setting_type = 'order_status'
        LEFT JOIN settings pm ON o.payment_method_id = pm.setting_id AND pm.setting_type = 'payment_method'
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
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setDiscount(rs.getInt("discount"));

                // Tạo đối tượng Setting cho status
                Setting status = new Setting();
                status.setSettingId(rs.getInt("setting_id"));
                status.setName(rs.getString("status_name"));
                status.setDescription(rs.getString("status_description"));
                order.setStatus(status);

                // Tạo đối tượng Setting cho payment method
                Setting paymentMethod = new Setting();
                paymentMethod.setSettingId(rs.getInt("payment_method_id"));
                paymentMethod.setName(rs.getString("payment_method_name"));
                paymentMethod.setDescription(rs.getString("payment_method_description"));
                order.setPaymentMethod(paymentMethod);

                // Load order items cho mỗi order
                order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = """
        SELECT oi.order_item_id, oi.quantity, oi.price, 
               p.product_id, p.product_name, p.image, p.brief_info, 
               p.description, p.color, p.memory, p.price_sale, p.price_origin
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

                    // Tạo đối tượng Product với thông tin chi tiết
                    Product product = new Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setImage(rs.getString("image"));
                    product.setBriefInfo(rs.getString("brief_info"));
                    product.setDescription(rs.getString("description"));
                    product.setColor(rs.getString("color"));
                    product.setMemory(rs.getString("memory"));
                    product.setPriceSale(rs.getLong("price_sale"));
                    product.setPriceOrigin(rs.getLong("price_origin"));

                    // Set product cho OrderItem
                    orderItem.setProduct(product);

                    // Vẫn giữ các trường transient để backward compatibility
                    orderItem.setProductName(rs.getString("product_name"));
                    orderItem.setProductImageUrl(rs.getString("image"));

                    orderItems.add(orderItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    public Order getOrderById(Integer orderId) {
        String sql = """
        SELECT o.order_id, o.user_id, o.email, o.full_name, o.address, 
               o.note, o.phone_number, o.total_money, o.order_date, o.discount,
               s.setting_id, s.name as status_name, s.description as status_description,
               pm.setting_id as payment_method_id, pm.name as payment_method_name, pm.description as payment_method_description
        FROM orders o
        LEFT JOIN settings s ON o.status_id = s.setting_id AND s.setting_type = 'order_status'
        LEFT JOIN settings pm ON o.payment_method_id = pm.setting_id AND pm.setting_type = 'payment_method'
        WHERE o.order_id = ? AND o.is_deleted = 0
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setEmail(rs.getString("email"));
                    order.setFullName(rs.getString("full_name"));
                    order.setAddress(rs.getString("address"));
                    order.setNote(rs.getString("note"));
                    order.setPhoneNumber(rs.getString("phone_number"));
                    order.setTotalMoney(rs.getLong("total_money"));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    order.setDiscount(rs.getInt("discount"));

                    // Tạo đối tượng Setting cho status
                    Setting status = new Setting();
                    status.setSettingId(rs.getInt("setting_id"));
                    status.setName(rs.getString("status_name"));
                    status.setDescription(rs.getString("status_description"));
                    order.setStatus(status);

                    // Tạo đối tượng Setting cho payment method
                    Setting paymentMethod = new Setting();
                    paymentMethod.setSettingId(rs.getInt("payment_method_id"));
                    paymentMethod.setName(rs.getString("payment_method_name"));
                    paymentMethod.setDescription(rs.getString("payment_method_description"));
                    order.setPaymentMethod(paymentMethod);

                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Hủy đơn hàng bằng cách cập nhật status_id thành trạng thái "cancelled"
     * @param orderId ID của đơn hàng cần hủy
     * @return true nếu hủy thành công, false nếu thất bại
     */
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

    /**
     * Kiểm tra xem đơn hàng có thể hủy được không
     * @param orderId ID của đơn hàng
     * @return true nếu có thể hủy, false nếu không thể hủy
     */
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

    public static void main(String[] args) {
        OrderDAO orderDAO = new OrderDAO();
        System.out.println(orderDAO.cancelOrder(1));
    }
}