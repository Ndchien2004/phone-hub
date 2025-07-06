package controller.home;

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

@WebServlet(name = "AddToCartServlet", urlPatterns = {"/add-to-cart"})
public class AddToCartServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        try {
            // Lấy dữ liệu từ request
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity")); // mặc định thường là 1
            long price = Long.parseLong(request.getParameter("price")); // giá sale

            // Lấy user hiện tại (có thể là guest)
            User currentUser = (User) session.getAttribute("user");
            Integer userId = currentUser != null ? currentUser.getUserId() : null;

            // Kiểm tra giỏ hàng hiện tại
            Integer cartId = (Integer) session.getAttribute("cartId");

            if (cartId == null) {
                cartId = cartService.createCart(userId); // tạo mới nếu chưa có
                session.setAttribute("cartId", cartId);
            }

            // Gọi service để thêm sản phẩm
            cartService.addProductToCart(cartId, productId, quantity, price);

            CartDTO cartDTO = cartService.getCartByUserIdOrCartId(userId, cartId);
            session.setAttribute("cart", cartDTO); // Lưu vào session

            // Cập nhật lại số lượng cartCount
            int cartCount = cartService.getTotalQuantityInCart(cartId);
            session.setAttribute("cartCount", cartCount);

            // Redirect về trang home
            response.sendRedirect(request.getContextPath() + "/home?added=true");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input parameters.");
        }
    }

}
