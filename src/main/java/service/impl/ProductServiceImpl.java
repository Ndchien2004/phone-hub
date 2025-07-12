package service.impl;
import model.dao.ProductDAO;
import model.dao.SettingDAO;
import model.dao.impl.ProductDAOImpl;
import model.dao.impl.SettingDAOImpl;
import model.entity.Product;
import model.entity.Setting;
import service.ProductService;
import config.CloudinaryConfig;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Base64;

public class ProductServiceImpl implements ProductService {
    private final ProductDAO productDAO = new ProductDAOImpl();
    private final SettingDAO settingDAO = new SettingDAOImpl();
    private final Cloudinary cloudinary;
    
    public ProductServiceImpl() {
        // Initialize Cloudinary with configuration
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CloudinaryConfig.CLOUD_NAME,
            "api_key", CloudinaryConfig.API_KEY,
            "api_secret", CloudinaryConfig.API_SECRET
        ));
    }

    @Override
    public Optional<Product> getProductById(int id) {
        return productDAO.findById(id);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
    
    @Override
    public List<Product> getProductsByCategory(int categoryId) {
        return productDAO.findByCategory(categoryId);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        return productDAO.searchByName(keyword);
    }
    
    @Override
    public List<Product> getProductsWithPagination(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return productDAO.findWithPagination(offset, pageSize);
    }
    
    @Override
    public int getTotalProductCount() {
        return productDAO.getTotalProducts();
    }
    
    @Override
    public boolean createProduct(Product product) {
        // Validate product data
        if (!isValidProduct(product)) {
            return false;
        }
        
        // Check for duplicate
        if (isProductNameExists(product.getProductName(), product.getColor(), product.getMemory(), null)) {
            return false;
        }
        
        return productDAO.save(product);
    }
    
    @Override
    public boolean updateProduct(Product product) {
        // Validate product data
        if (!isValidProduct(product)) {
            return false;
        }
        
        // Check for duplicate (excluding current product)
        if (isProductNameExists(product.getProductName(), product.getColor(), product.getMemory(), product.getProductId())) {
            return false;
        }
        
        return productDAO.update(product);
    }
    
    @Override
    public boolean deleteProduct(int id) {
        return productDAO.delete(id);
    }
    
    @Override
    public boolean isProductNameExists(String name, String color, String memory, Integer excludeId) {
        return productDAO.existsByNameColorMemory(name, color, memory, excludeId);
    }
    
    @Override
    public List<Setting> getCategories() {
        return settingDAO.findByType("CATEGORY");
    }
    
    @Override
    public String uploadProductImage(String base64Image, String fileName) {
        try {
            // Remove data:image/jpeg;base64, prefix if present
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }
            
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                "folder", CloudinaryConfig.UPLOAD_FOLDER,
                "public_id", fileName,
                "overwrite", CloudinaryConfig.OVERWRITE,
                "resource_type", CloudinaryConfig.RESOURCE_TYPE
            ));
            
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean isValidProduct(Product product) {
        return product != null
            && product.getProductName() != null && !product.getProductName().trim().isEmpty()
            && product.getColor() != null && !product.getColor().trim().isEmpty()
            && product.getMemory() != null && !product.getMemory().trim().isEmpty()
            && product.getCategoryId() != null
            && product.getQuantity() >= 0
            && product.getPriceSale() != null && product.getPriceSale() > 0
            && product.getPriceOrigin() != null && product.getPriceOrigin() > 0;
    }
}