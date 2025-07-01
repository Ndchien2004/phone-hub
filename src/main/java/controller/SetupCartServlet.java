package controller;

import model.entity.Cart;
import model.entity.CartItem;
import model.entity.Product;
import service.ProductService;
import service.impl.ProductServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * Đây là một servlet dùng để test.
 * Nó sẽ tự động lấy một sản phẩm (ID = 1), tạo giỏ hàng,
 * thêm sản phẩm đó vào giỏ, lưu vào session, và chuyển hướng đến trang /checkout.
 */
@WebServlet("/setup-cart")
public class SetupCartServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--- [SetupCartServlet] Bắt đầu xử lý ---");
        try {
            // Bước 1: Gọi ProductService để lấy thông tin sản phẩm
            // Sử dụng Optional để xử lý trường hợp không tìm thấy sản phẩm một cách an toàn
            Optional<Product> productOptional = productService.getProductById(1);

            if (!productOptional.isPresent()) {
                System.err.println("LỖI: Không tìm thấy sản phẩm có ID = 1 trong cơ sở dữ liệu.");
                // Có thể chuyển hướng đến một trang lỗi nếu muốn
                resp.getWriter().write("Error: Product with ID 1 not found.");
                return;
            }

            // Nếu tìm thấy, lấy đối tượng Product ra
            Product product = productOptional.get();
            System.out.println("Đã lấy thành công sản phẩm: " + product.getProductName());

            // Bước 2: Tạo đối tượng Cart và CartItem
            Cart cart = new Cart();
            CartItem cartItem = new CartItem();

            cartItem.setProductId(product.getProductId());
            cartItem.setQuantity(1); // Mặc định số lượng là 1 để test
            cartItem.setPrice(product.getPriceSale()); // Lấy giá bán từ sản phẩm
            cartItem.setProduct(product); // Gắn đối tượng product để tiện hiển thị ở JSP

            System.out.println("Đã tạo CartItem cho sản phẩm: " + product.getProductName() + " với giá: " + cartItem.getPrice());

            // Bước 3: Thêm CartItem vào Cart
            cart.addItem(cartItem);
            cart.calculateTotals(); // Tính toán tất cả các giá trị tổng tiền
            System.out.println("Đã thêm item vào giỏ hàng. Tổng tiền cuối cùng: " + cart.getFinalTotal());
            System.out.println("Đã thêm item vào giỏ hàng. Tổng số loại sản phẩm: " + cart.getItems().size());

            // Bước 4: Lưu đối tượng Cart vào session
            HttpSession session = req.getSession();
            session.setAttribute("cart", cart);
            System.out.println("Đã lưu giỏ hàng vào session.");

            // Bước 5: Chuyển hướng đến trang checkout
            System.out.println("--- [SetupCartServlet] Hoàn tất, chuyển hướng đến /checkout ---");
            resp.sendRedirect("checkout");

        } catch (Exception e) {
            System.err.println("--- !!! ĐÃ XẢY RA LỖI NGHIÊM TRỌNG TRONG SetupCartServlet !!! ---");
            e.printStackTrace();
            // Chuyển hướng đến trang lỗi chung
            resp.sendRedirect("error.jsp");
        }
    }
}