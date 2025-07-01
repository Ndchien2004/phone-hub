package model.dao.impl;

import model.dao.DBContext;
import model.dao.ProductDAO;
import model.entity.Product;
import java.sql.*;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {
    @Override
    public Optional<Product> findById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    // ĐIỀN ĐẦY ĐỦ DỮ LIỆU
                    product.setProductId(rs.getInt("product_id"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setProductName(rs.getString("product_name")); // Quan trọng
                    product.setImageUrl(rs.getString("image")); // Quan trọng
                    product.setBriefInfo(rs.getString("brief_info"));
                    product.setDescription(rs.getString("description"));
                    product.setColor(rs.getString("color"));
                    product.setMemory(rs.getString("memory"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setPriceSale(rs.getLong("price_sale"));
                    product.setPriceOrigin(rs.getLong("price_origin"));
                    return Optional.of(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}