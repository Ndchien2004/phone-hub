package service;

import model.dto.ProductDTO;
import model.entity.Setting;
import util.PageResult;

import java.math.BigDecimal;
import java.util.List;

public interface HomeService {
    PageResult<ProductDTO> getAllProducts(int page, int pageSize);
    List<Setting> getAllCategories();
    PageResult<ProductDTO> searchByName(String keyword, int page, int pageSize);
    PageResult<ProductDTO> searchByCategory(String categoryName, int page, int pageSize);
    PageResult<ProductDTO> searchByPriceRange(BigDecimal min, BigDecimal max, int page, int pageSize);
}
