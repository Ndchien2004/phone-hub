package controller.manager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.entity.Product;
import model.entity.Setting;
import service.ProductService;
import service.impl.ProductServiceImpl;
import util.JsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@WebServlet("/manager/products")
@MultipartConfig
public class ManagerProductServlet extends HttpServlet {
    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String PAGE_TITLE_ATTR = "pageTitle";
    private static final String CONTENT_PAGE_ATTR = "contentPage";
    private static final String LAYOUT_PATH = "/views/manager/enhanced-layout.jsp";
    private final ProductService productService = new ProductServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    handleListProducts(request, response);
                    break;
                case "add":
                    showAddProductForm(request, response);
                    break;
                case "edit":
                    showEditProductForm(request, response);
                    break;
                case "view":
                    viewProduct(request, response);
                    break;
                case "search":
                    searchProducts(request, response);
                    break;
                default:
                    handleListProducts(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    createProduct(request, response);
                    break;
                case "update":
                    updateProduct(request, response);
                    break;
                case "delete":
                    deleteProduct(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
    private void handleListProducts(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int page = 1;
        int pageSize = 10;
        
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
            String pageSizeParam = request.getParameter("pageSize");
            if (pageSizeParam != null) {
                pageSize = Integer.parseInt(pageSizeParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
            pageSize = 10;
        }
        
        List<Product> products = productService.getProductsWithPagination(page, pageSize);
        int totalProducts = productService.getTotalProductCount();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        
        request.setAttribute("products", products);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalProducts", totalProducts);
        
        // Set layout attributes
        request.setAttribute(PAGE_TITLE_ATTR, "Product Management");
        request.setAttribute(CONTENT_PAGE_ATTR, "/views/manager/products/list.jsp");
        request.getRequestDispatcher(LAYOUT_PATH).forward(request, response);
    }
    
    private void showAddProductForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Setting> categories = productService.getCategories();
        request.setAttribute("categories", categories);
        
        // Set layout attributes
        request.setAttribute(PAGE_TITLE_ATTR, "Add Product");
        request.setAttribute(CONTENT_PAGE_ATTR, "/views/manager/products/add.jsp");
        request.getRequestDispatcher(LAYOUT_PATH).forward(request, response);
    }
    
    private void showEditProductForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int productId = Integer.parseInt(request.getParameter("id"));
        Product product = productService.getProductById(productId).orElse(null);
        
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, PRODUCT_NOT_FOUND);
            return;
        }
        
        List<Setting> categories = productService.getCategories();
        request.setAttribute("product", product);
        request.setAttribute("categories", categories);
        
        // Set layout attributes
        request.setAttribute(PAGE_TITLE_ATTR, "Edit Product");
        request.setAttribute(CONTENT_PAGE_ATTR, "/views/manager/products/edit.jsp");
        request.getRequestDispatcher(LAYOUT_PATH).forward(request, response);
    }
    
    private void viewProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int productId = Integer.parseInt(request.getParameter("id"));
        Product product = productService.getProductById(productId).orElse(null);
        
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, PRODUCT_NOT_FOUND);
            return;
        }
        
        request.setAttribute("product", product);
        
        // Set layout attributes
        request.setAttribute(PAGE_TITLE_ATTR, "View Product");
        request.setAttribute(CONTENT_PAGE_ATTR, "/views/manager/products/view.jsp");
        request.getRequestDispatcher(LAYOUT_PATH).forward(request, response);
    }
    
    private void searchProducts(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Product> products = productService.searchProducts(keyword != null ? keyword : "");
        
        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        
        // Set layout attributes
        request.setAttribute(PAGE_TITLE_ATTR, "Product Management");
        request.setAttribute(CONTENT_PAGE_ATTR, "/views/manager/products/list.jsp");
        request.getRequestDispatcher(LAYOUT_PATH).forward(request, response);
    }
    
    private void createProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Product product = new Product();
        
        // Set basic product information
        product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        product.setProductName(request.getParameter("productName"));
        product.setBriefInfo(request.getParameter("briefInfo"));
        product.setDescription(request.getParameter("description"));
        product.setColor(request.getParameter("color"));
        product.setMemory(request.getParameter("memory"));
        product.setQuantity(Integer.parseInt(request.getParameter("quantity")));
        product.setPriceSale(Long.parseLong(request.getParameter("priceSale")));
        product.setPriceOrigin(Long.parseLong(request.getParameter("priceOrigin")));
        
        // Handle image upload
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            String imageUrl = handleImageUpload(imagePart, product.getProductName());
            if (imageUrl != null) {
                product.setImageUrl(imageUrl);
            }
        }
        
        boolean success = productService.createProduct(product);
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/manager/products?success=created");
        } else {
            request.setAttribute("error", "Failed to create product. Please check if product already exists.");
            showAddProductForm(request, response);
        }
    }
    
    private void updateProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        Product product = productService.getProductById(productId).orElse(null);
        
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, PRODUCT_NOT_FOUND);
            return;
        }
        
        // Update product information
        product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        product.setProductName(request.getParameter("productName"));
        product.setBriefInfo(request.getParameter("briefInfo"));
        product.setDescription(request.getParameter("description"));
        product.setColor(request.getParameter("color"));
        product.setMemory(request.getParameter("memory"));
        product.setQuantity(Integer.parseInt(request.getParameter("quantity")));
        product.setPriceSale(Long.parseLong(request.getParameter("priceSale")));
        product.setPriceOrigin(Long.parseLong(request.getParameter("priceOrigin")));
        product.setUpdatedAt(LocalDateTime.now());
        
        // Handle image upload
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            String imageUrl = handleImageUpload(imagePart, product.getProductName());
            if (imageUrl != null) {
                product.setImageUrl(imageUrl);
            }
        }
        
        boolean success = productService.updateProduct(product);
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/manager/products?success=updated");
        } else {
            request.setAttribute("error", "Failed to update product. Please check if product name already exists.");
            showEditProductForm(request, response);
        }
    }
    
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        boolean success = productService.deleteProduct(productId);
        
        response.setContentType("application/json");
        response.getWriter().write(JsonUtil.toJson(success));
    }
    
    private String handleImageUpload(Part imagePart, String productName) {
        try {
            InputStream inputStream = imagePart.getInputStream();
            byte[] imageBytes = inputStream.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            String fileName = productName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis();
            return productService.uploadProductImage(base64Image, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
