package model.dao.impl;

import model.dao.DBContext;
import model.entity.CartItem;
import model.entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAOImpl {

    public CartItem findById(int cartItemId) {
        String query = "SELECT * FROM cart_items WHERE cart_item_id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, cartItemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(cartItemId);
                item.setCartId(rs.getInt("cart_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getLong("price"));
                return item;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CartItem> findByCartId(int cartId, Connection conn) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String itemQuery = """
                SELECT ci.*, p.product_name, p.image, p.color, p.memory, p.price_sale, p.price_origin
                FROM cart_items ci
                JOIN products p ON ci.product_id = p.product_id
                WHERE ci.cart_id = ? AND ci.is_deleted = 0
                """;
        try (PreparedStatement ps = conn.prepareStatement(itemQuery)) {
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("cart_item_id"));
                item.setCartId(cartId);
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getLong("price"));

                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setImageUrl(rs.getString("image"));
                product.setColor(rs.getString("color"));
                product.setMemory(rs.getString("memory"));
                product.setPriceSale(rs.getLong("price_sale"));
                product.setPriceOrigin(rs.getLong("price_origin"));

                item.setProduct(product);
                items.add(item);
            }
        }
        return items;
    }

    public void updateQuantityById(int cartItemId, int quantity) {
        String query = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(int cartItemId) {
        String query = "UPDATE cart_items SET is_deleted = 1 WHERE cart_item_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByCartId(int cartId) {
        String query = "DELETE FROM cart_items WHERE cart_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
