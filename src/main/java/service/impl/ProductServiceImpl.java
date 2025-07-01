package service.impl;
import model.dao.ProductDAO;
import model.dao.impl.ProductDAOImpl;
import model.entity.Product;
import service.ProductService;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {
    private final ProductDAO productDAO = new ProductDAOImpl();

    @Override
    public Optional<Product> getProductById(int id) {
        return productDAO.findById(id);
    }
}