package controller.cart; // Hoặc package của bạn

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.CartDTO;
import model.entity.User;
import service.CartService;

import java.io.IOException;

@WebServlet(name = "BuyNowServlet", urlPatterns = {"/buy-now"})
public class BuyNowServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = 1;

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            Integer cartId = (Integer) session.getAttribute("cartId");

            // ======================= LỜI GỌI DUY NHẤT, ĐƠN GIẢN =======================
            // Gọi phương thức addToCart mới, nó sẽ tự xử lý mọi logic phức tạp
            CartDTO updatedCart = cartService.addToCart(user, cartId, productId, quantity);
            // ========================================================================

            // Cập nhật session với thông tin từ giỏ hàng mới
            session.setAttribute("cartId", updatedCart.getCartId());
            if (updatedCart.getItems() != null) {
                session.setAttribute("cartCount", updatedCart.getItems().size());
            }

            // Chuyển hướng đến luồng thanh toán
            response.sendRedirect(request.getContextPath() + "/proceed-to-checkout");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home?error=buy_now_failed");
        }
    }
}