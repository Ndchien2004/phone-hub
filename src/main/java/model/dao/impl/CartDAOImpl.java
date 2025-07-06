package model.dao.impl;

import model.entity.Cart;
import model.dao.DBContext;

import java.sql.*;

public class CartDAOImpl {

    private final CartItemDAOImpl cartItemDAO = new CartItemDAOImpl();

    public Cart findById(int cartId) {
        Cart cart = null;
        try (Connection conn = DBContext.getConnection()) {
            String sql = "SELECT * FROM carts WHERE cart_id = ? AND is_deleted = 0";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, cartId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    cart = new Cart();
                    cart.setCartId(rs.getInt("cart_id"));
                    cart.setUserId((Integer) rs.getObject("user_id")); // user_id có thể null
                    cart.setTotalMoney(rs.getLong("total_money"));

                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        cart.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    // Lấy danh sách cart items
                    cart.setItems(cartItemDAO.findByCartId(cartId, conn));

                    // Cập nhật lại tổng tiền (rawTotal, finalTotal, ...)
                    cart.calculateTotals();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }

    public Cart findByUserId(int userId) {
        Cart cart = null;
        try (Connection conn = DBContext.getConnection()) {
            String cartQuery = "SELECT * FROM carts WHERE user_id = ? AND is_deleted = 0";
            try (PreparedStatement ps = conn.prepareStatement(cartQuery)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    cart = new Cart();
                    cart.setCartId(rs.getInt("cart_id"));
                    cart.setUserId(userId);
                    cart.setTotalMoney(rs.getLong("total_money"));
                    cart.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    cart.setItems(cartItemDAO.findByCartId(cart.getCartId(), conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }

    public void addProductToCart(int cartId, int productId, int quantity, long price) {
        try (Connection conn = DBContext.getConnection()) {
            String checkQuery = "SELECT quantity FROM cart_items WHERE cart_id = ? AND product_id = ? AND is_deleted = 0";
            try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int existingQty = rs.getInt("quantity");
                    String updateQuery = "UPDATE cart_items SET quantity = ?, price = ? WHERE cart_id = ? AND product_id = ?";
                    try (PreparedStatement ups = conn.prepareStatement(updateQuery)) {
                        ups.setInt(1, existingQty + quantity);
                        ups.setLong(2, price);
                        ups.setInt(3, cartId);
                        ups.setInt(4, productId);
                        ups.executeUpdate();
                    }
                } else {
                    String insertQuery = "INSERT INTO cart_items (cart_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ips = conn.prepareStatement(insertQuery)) {
                        ips.setInt(1, cartId);
                        ips.setInt(2, productId);
                        ips.setInt(3, quantity);
                        ips.setLong(4, price);
                        ips.executeUpdate();
                    }
                }
            }
            updateCartTotal(cartId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCartTotal(int cartId) {
        String totalQuery = "SELECT SUM(quantity * price) AS total FROM cart_items WHERE cart_id = ? AND is_deleted = 0";
        String updateCart = "UPDATE carts SET total_money = ? WHERE cart_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(totalQuery);
             PreparedStatement ps2 = conn.prepareStatement(updateCart)) {
            ps1.setInt(1, cartId);
            ResultSet rs = ps1.executeQuery();
            long total = 0;
            if (rs.next()) {
                total = rs.getLong("total");
            }
            ps2.setLong(1, total);
            ps2.setInt(2, cartId);
            ps2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //The input is integer instead of int for null allowed
    public int createCart(Integer userId) {
        String query = "INSERT INTO carts (user_id) OUTPUT INSERTED.cart_id VALUES (?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (userId != null) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("cart_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteCartById(int cartId) {
        String query = "UPDATE carts SET is_deleted = 1 WHERE cart_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
