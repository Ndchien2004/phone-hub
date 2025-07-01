package model.dao;
import model.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    // Read operations
    Optional<Product> findById(int id);
    List<Product> findAll();
    List<Product> findByCategory(int categoryId);
    List<Product> searchByName(String keyword);
    int getTotalProducts();
    List<Product> findWithPagination(int offset, int limit);
    
    // Write operations
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(int id); // Soft delete
    
    // Validation
    boolean existsByNameColorMemory(String name, String color, String memory, Integer excludeId);
}