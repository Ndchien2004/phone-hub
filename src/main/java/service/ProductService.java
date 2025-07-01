package service;
import model.entity.Product;
import model.entity.Setting;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    // Read operations
    Optional<Product> getProductById(int id);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(int categoryId);
    List<Product> searchProducts(String keyword);
    List<Product> getProductsWithPagination(int page, int pageSize);
    int getTotalProductCount();
    
    // Write operations
    boolean createProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    
    // Validation and utilities
    boolean isProductNameExists(String name, String color, String memory, Integer excludeId);
    List<Setting> getCategories();
    
    // Cloudinary image upload
    String uploadProductImage(String base64Image, String fileName);
}