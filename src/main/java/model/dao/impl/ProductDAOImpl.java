package model.dao.impl;

import model.dao.DBContext;
import model.dao.ProductDAO;
import model.entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {
    
    @Override
    public Optional<Product> findById(int id) {
        String sql = "SELECT product_id, category_id, product_name, image, brief_info, description, color, memory, quantity, price_sale, price_origin, created_at, updated_at, is_deleted FROM products WHERE product_id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Product> findAll() {
        String sql = "SELECT product_id, category_id, product_name, image, brief_info, description, color, memory, quantity, price_sale, price_origin, created_at, updated_at, is_deleted FROM products WHERE is_deleted = 0 ORDER BY created_at DESC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    @Override
    public List<Product> findByCategory(int categoryId) {
        String sql = "SELECT product_id, category_id, product_name, image, brief_info, description, color, memory, quantity, price_sale, price_origin, created_at, updated_at, is_deleted FROM products WHERE category_id = ? AND is_deleted = 0 ORDER BY created_at DESC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    @Override
    public List<Product> searchByName(String keyword) {
        String sql = "SELECT product_id, category_id, product_name, image, brief_info, description, color, memory, quantity, price_sale, price_origin, created_at, updated_at, is_deleted FROM products WHERE product_name LIKE ? AND is_deleted = 0 ORDER BY created_at DESC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public int getTotalProducts() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_deleted = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Product> findWithPagination(int offset, int limit) {
        String sql = "SELECT product_id, category_id, product_name, image, brief_info, description, color, memory, quantity, price_sale, price_origin, created_at, updated_at, is_deleted FROM products WHERE is_deleted = 0 ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public boolean save(Product product) {
        String sql = "INSERT INTO products (category_id, product_name, image, brief_info, description, color, memory, quantity, price_sale, price_origin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getImageUrl());
            ps.setString(4, product.getBriefInfo());
            ps.setString(5, product.getDescription());
            ps.setString(6, product.getColor());
            ps.setString(7, product.getMemory());
            ps.setInt(8, product.getQuantity());
            ps.setLong(9, product.getPriceSale());
            ps.setLong(10, product.getPriceOrigin());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET category_id = ?, product_name = ?, image = ?, brief_info = ?, description = ?, color = ?, memory = ?, quantity = ?, price_sale = ?, price_origin = ?, updated_at = GETDATE() WHERE product_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getImageUrl());
            ps.setString(4, product.getBriefInfo());
            ps.setString(5, product.getDescription());
            ps.setString(6, product.getColor());
            ps.setString(7, product.getMemory());
            ps.setInt(8, product.getQuantity());
            ps.setLong(9, product.getPriceSale());
            ps.setLong(10, product.getPriceOrigin());
            ps.setInt(11, product.getProductId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "UPDATE products SET is_deleted = 1 WHERE product_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsByNameColorMemory(String name, String color, String memory, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_name = ? AND color = ? AND memory = ? AND is_deleted = 0";
        if (excludeId != null) {
            sql += " AND product_id != ?";
        }

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, color);
            ps.setString(3, memory);
            if (excludeId != null) {
                ps.setInt(4, excludeId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setProductName(rs.getString("product_name"));
        product.setImageUrl(rs.getString("image"));
        product.setBriefInfo(rs.getString("brief_info"));
        product.setDescription(rs.getString("description"));
        product.setColor(rs.getString("color"));
        product.setMemory(rs.getString("memory"));
        product.setQuantity(rs.getInt("quantity"));
        product.setPriceSale(rs.getLong("price_sale"));
        product.setPriceOrigin(rs.getLong("price_origin"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        product.setDeleted(rs.getBoolean("is_deleted"));
        return product;
    }
}