package service;
import model.entity.Product;
import java.util.Optional;

public interface ProductService {
    Optional<Product> getProductById(int id);
}