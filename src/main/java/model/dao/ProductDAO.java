package model.dao;
import model.entity.Product;
import java.util.Optional;

public interface ProductDAO {
    Optional<Product> findById(int id);
}