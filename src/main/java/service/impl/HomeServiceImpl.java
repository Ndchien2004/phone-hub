package service.impl;

import model.dao.ProductDAO;
import model.dao.SettingDAO;
import model.dao.impl.ProductDAOImpl;
import model.dao.impl.SettingDAOImpl;
import model.dto.ProductDTO;
import model.entity.Product;
import model.entity.Setting;
import service.HomeService;
import util.PageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HomeServiceImpl implements HomeService {

    private final ProductDAO productDAO = new ProductDAOImpl();
    private final SettingDAO settingDAO = new SettingDAOImpl();

    @Override
    public PageResult<ProductDTO> getAllProducts(int page, int pageSize) {
        int totalElements = productDAO.getTotalProducts();
        int offset = (page - 1) * pageSize;
        List<Product> products = productDAO.findWithPagination(offset, pageSize);

        List<ProductDTO> content = products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(content, page, pageSize, totalElements);
    }

    @Override
    //Search by Name, Category, Price
    public PageResult<ProductDTO> search(String keyword, List<Integer> categoryId, BigDecimal min, BigDecimal max, int page, int pageSize) {
        List<Product> products = productDAO.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getProductName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (categoryId != null && !categoryId.isEmpty()) {
            products = products.stream()
                    .filter(p -> categoryId.contains(p.getCategoryId()))
                    .collect(Collectors.toList());
        }

        if (min != null || max != null) {
            products = products.stream()
                    .filter(p -> {
                        BigDecimal price = BigDecimal.valueOf(p.getPriceSale());
                        return (min == null || price.compareTo(min) >= 0) &&
                                (max == null || price.compareTo(max) <= 0);
                    })
                    .collect(Collectors.toList());
        }

        return paginateAndConvert(products, page, pageSize);
    }

    public List<Setting> getAllCategories() {
        return settingDAO.findByType("category");
    }

    private PageResult<ProductDTO> paginateAndConvert(List<Product> products, int page, int pageSize) {
        int totalElements = products.size();
        int fromIndex = Math.min((page - 1) * pageSize, totalElements);
        int toIndex = Math.min(fromIndex + pageSize, totalElements);

        List<ProductDTO> content = products.subList(fromIndex, toIndex).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(content, page, pageSize, totalElements);
    }

    private ProductDTO toDTO(Product p) {
        String categoryName = settingDAO.findById(p.getCategoryId())
                .map(Setting::getName)
                .orElse("Unknown");

        return new ProductDTO(
                p.getProductId() != null ? p.getProductId().longValue() : null,
                p.getProductName(),
                p.getBriefInfo(),
                p.getDescription(),
                BigDecimal.valueOf(p.getPriceOrigin()),
                BigDecimal.valueOf(p.getPriceSale()),
                p.getImageUrl(),
                categoryName,
                p.getQuantity()
        );
    }
}
