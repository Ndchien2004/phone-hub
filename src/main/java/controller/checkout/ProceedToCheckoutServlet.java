package controller.checkout;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.CartDTO;
import model.dto.CartItemDTO; // Giả định đồng đội bạn có class này
import model.entity.Cart;
import model.entity.CartItem;
import model.entity.Product;
import service.CartService; // Service của đồng đội
import service.ProductService; // Service của bạn
import service.impl.ProductServiceImpl;

import java.io.IOException;
import java.util.Optional;

/**
 * Servlet này là "cầu nối" giữa trang giỏ hàng và trang thanh toán.
 * Nó lấy thông tin giỏ hàng từ session (do CartServlet của đồng đội tạo),
 * chuyển đổi nó thành đối tượng Cart mà hệ thống checkout cần,
 * sau đó chuyển hướng đến trang checkout.
 */
@WebServlet("/proceed-to-checkout")
public class ProceedToCheckoutServlet extends HttpServlet {

    private final CartService cartService = new CartService(); // Service của đồng đội
    private final ProductService productService = new ProductServiceImpl(); // Service của bạn

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--- [ProceedToCheckoutServlet] Bắt đầu xử lý ---");
        HttpSession session = req.getSession(false);

        // Kiểm tra xem session hoặc cartId có tồn tại không
        if (session == null || session.getAttribute("cartId") == null) {
            System.err.println("Lỗi: Không tìm thấy session hoặc cartId. Không thể tiến hành thanh toán.");
            // Chuyển hướng người dùng về lại trang giỏ hàng với thông báo lỗi
            resp.sendRedirect("cart?error=notfound");
            return;
        }

        try {
            Integer cartId = (Integer) session.getAttribute("cartId");
            System.out.println("Đã lấy cartId từ session: " + cartId);

            // Bước 1: Dùng service của đồng đội để lấy CartDTO từ database
            CartDTO cartDTO = cartService.getCartByUserIdOrCartId(null, cartId);

            if (cartDTO == null || cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
                System.err.println("Lỗi: Giỏ hàng (ID: " + cartId + ") rỗng hoặc không tồn tại trong DB.");
                resp.sendRedirect("cart?error=empty");
                return;
            }
            System.out.println("Đã lấy được CartDTO từ DB với " + cartDTO.getItems().size() + " loại sản phẩm.");

            // Bước 2: Chuyển đổi từ CartDTO (của đồng đội) sang Cart (của bạn)
            Cart checkoutCart = new Cart();
            for (CartItemDTO itemDTO : cartDTO.getItems()) {
                // Với mỗi item trong DTO, lấy đầy đủ thông tin Product
                Optional<Product> productOptional = productService.getProductById(itemDTO.getProductId());

                if (productOptional.isPresent()) {
                    Product product = productOptional.get();

                    // Tạo CartItem mà hệ thống checkout của bạn cần
                    CartItem cartItem = new CartItem();
                    cartItem.setProductId(product.getProductId());
                    cartItem.setQuantity(itemDTO.getQuantity());
                    cartItem.setPrice(product.getPriceSale()); // Lấy giá mới nhất từ DB để đảm bảo chính xác
                    cartItem.setProduct(product); // Gắn đối tượng Product đầy đủ

                    checkoutCart.addItem(cartItem);
                } else {
                    System.err.println("Cảnh báo: Sản phẩm ID " + itemDTO.getProductId() + " trong giỏ hàng không còn tồn tại. Bỏ qua sản phẩm này.");
                }
            }

            // Tính toán lại tổng tiền dựa trên dữ liệu mới
            checkoutCart.calculateTotals();
            System.out.println("Đã chuyển đổi thành công sang đối tượng Cart cho checkout. Tổng tiền: " + checkoutCart.getFinalTotal());

            // Bước 3: Lưu đối tượng Cart (của bạn) vào session để CheckoutServlet sử dụng
            session.setAttribute("cart", checkoutCart);
            System.out.println("Đã lưu đối tượng 'cart' vào session.");

            // Bước 4: Chuyển hướng đến servlet checkout của bạn
            System.out.println("--- [ProceedToCheckoutServlet] Hoàn tất, chuyển hướng đến /checkout ---");
            resp.sendRedirect("checkout");

        } catch (Exception e) {
            System.err.println("--- !!! LỖI NGHIÊM TRỌNG trong ProceedToCheckoutServlet !!! ---");
            e.printStackTrace();
            // Chuyển hướng đến trang lỗi
            resp.sendRedirect("error.jsp");
        }
    }
}