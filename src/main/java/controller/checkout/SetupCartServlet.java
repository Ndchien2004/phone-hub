package controller.checkout;

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
 * Nó sẽ tự động lấy các sản phẩm có ID từ 1 đến 9, tạo giỏ hàng,
 * thêm các sản phẩm đó vào giỏ, lưu vào session, và chuyển hướng đến trang /checkout.
 */
@WebServlet("/setup-cart")
public class SetupCartServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--- [SetupCartServlet] Bắt đầu xử lý với nhiều sản phẩm ---");
        try {
            // Bước 1: Tạo một đối tượng Cart mới
            Cart cart = new Cart();
            System.out.println("Đã tạo một giỏ hàng mới.");

            // BƯỚC 2: BỔ SUNG - Dùng vòng lặp để lấy sản phẩm từ ID 1 đến 9
            for (int productId = 1; productId <= 2; productId++) {
                // Gọi ProductService để lấy thông tin sản phẩm
                Optional<Product> productOptional = productService.getProductById(productId);

                // Kiểm tra xem sản phẩm có tồn tại không
                if (productOptional.isPresent()) {
                    Product product = productOptional.get();
                    System.out.println("  -> Đã lấy thành công sản phẩm ID " + productId + ": " + product.getProductName());

                    // Tạo đối tượng CartItem cho sản phẩm này
                    CartItem cartItem = new CartItem();
                    cartItem.setProductId(product.getProductId());
                    cartItem.setQuantity(1); // Mặc định số lượng là 1 để test
                    cartItem.setPrice(product.getPriceSale()); // Lấy giá bán từ sản phẩm
                    cartItem.setProduct(product); // Gắn đối tượng product để tiện hiển thị ở JSP

                    // Thêm CartItem vào Cart
                    cart.addItem(cartItem);
                    System.out.println("     => Đã thêm '" + product.getProductName() + "' vào giỏ hàng.");

                } else {
                    // Nếu sản phẩm không tồn tại, chỉ in ra cảnh báo và tiếp tục vòng lặp
                    System.err.println("  -> CẢNH BÁO: Không tìm thấy sản phẩm có ID = " + productId + ". Bỏ qua sản phẩm này.");
                }
            } // Kết thúc vòng lặp

            // Kiểm tra xem giỏ hàng có rỗng không sau khi lặp
            if (cart.getItems().isEmpty()) {
                System.err.println("LỖI: Không thêm được sản phẩm nào vào giỏ hàng. Có thể tất cả các ID từ 1-9 đều không tồn tại.");
                resp.getWriter().write("Error: No products could be added to the cart. Please check the database.");
                return;
            }

            // Bước 3: Tính toán lại tổng tiền sau khi đã thêm tất cả sản phẩm
            cart.calculateTotals();
            System.out.println("Đã thêm tất cả các sản phẩm hợp lệ vào giỏ hàng. Tổng số loại sản phẩm: " + cart.getItems().size());
            System.out.println("Tổng tiền cuối cùng sau khi tính toán: " + cart.getFinalTotal());

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