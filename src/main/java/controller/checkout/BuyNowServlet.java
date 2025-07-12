package controller.checkout; // Hoặc package controller.cart tùy bạn sắp xếp

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.entity.Cart;
import model.entity.CartItem;
import model.entity.Product;
import service.ProductService;
import service.impl.ProductServiceImpl;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/buy-now")
public class BuyNowServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Bước 1: Lấy thông tin sản phẩm từ request
            int productId = Integer.parseInt(req.getParameter("productId"));
            System.out.println("[BuyNowServlet] Bắt đầu xử lý Mua Ngay cho sản phẩm ID: " + productId);

            // Bước 2: Lấy thông tin đầy đủ của sản phẩm từ database
            Optional<Product> productOptional = productService.getProductById(productId);

            if (productOptional.isEmpty()) {
                System.err.println("Lỗi: Không tìm thấy sản phẩm có ID = " + productId);
                resp.sendRedirect("home?error=product_not_found");
                return;
            }
            Product product = productOptional.get();

            // Bước 3: Tạo một giỏ hàng TẠM THỜI chỉ chứa sản phẩm này
            Cart buyNowCart = new Cart();

            CartItem cartItem = new CartItem();
            cartItem.setProductId(product.getProductId());
            cartItem.setQuantity(1); // Mua ngay mặc định số lượng là 1
            cartItem.setPrice(product.getPriceSale());
            cartItem.setProduct(product);

            buyNowCart.addItem(cartItem);
            buyNowCart.calculateTotals(); // Tính toán tổng tiền cho giỏ hàng tạm thời này

            // Bước 4: Lưu giỏ hàng tạm thời này vào session
            // **QUAN TRỌNG**: Nó sẽ ghi đè lên giỏ hàng hiện có của người dùng.
            // Đây là hành vi mong muốn của chức năng "Mua ngay".
            HttpSession session = req.getSession();
            session.setAttribute("cart", buyNowCart);
            System.out.println("Đã tạo và lưu giỏ hàng tạm thời cho Mua Ngay vào session.");

            // Bước 5: Chuyển thẳng đến trang checkout
            System.out.println("[BuyNowServlet] Hoàn tất, chuyển hướng đến /checkout");
            resp.sendRedirect("checkout");

        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Product ID không hợp lệ.");
            resp.sendRedirect("home?error=invalid_id");
        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng trong BuyNowServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendRedirect("error.jsp");
        }
    }
}