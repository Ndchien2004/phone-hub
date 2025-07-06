package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dto.CartDTO;
import model.entity.User;
import service.CartService;

import java.io.IOException;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        // Debug logging
        System.out.println("=== CART SERVLET DEBUG ===");
        System.out.println("Action: " + action);
        System.out.println("Session ID: " + session.getId());

        Integer cartId = (Integer) session.getAttribute("cartId");
        System.out.println("Cart ID from session: " + cartId);
        
        if (cartId == null) {
            System.out.println("No cartId found, redirecting to home");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // Xử lý action: remove, removeAll
        if ("remove".equals(action)) {
            try {
                int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
                System.out.println("Removing cart item: " + cartItemId);
                
                cartService.removeCartItem(cartItemId);
                System.out.println("Successfully removed cart item: " + cartItemId);
                
                request.setAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng!");
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error removing cart item: " + e.getMessage());
                request.setAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            }
        } else if ("removeAll".equals(action)) {
            try {
                System.out.println("Removing entire cart: " + cartId);
                cartService.deleteCart(cartId);
                session.removeAttribute("cartId");
                session.removeAttribute("cart");
                System.out.println("Successfully removed entire cart");
                
                request.setAttribute("success", "Đã xóa toàn bộ giỏ hàng!");
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error removing cart: " + e.getMessage());
                request.setAttribute("error", "Lỗi khi xóa giỏ hàng: " + e.getMessage());
            }
        }

        // Lấy lại cartDTO mới từ database
        User user = (User) session.getAttribute("user");
        Integer userId = user != null ? user.getUserId() : null;
        System.out.println("User ID: " + userId);

        CartDTO cart = cartService.getCartByUserIdOrCartId(userId, cartId);
        
        // Debug: Kiểm tra cart sau khi load từ DB
        System.out.println("Cart loaded from DB: " + cart);
        if (cart != null) {
            System.out.println("Cart items count: " + (cart.getItems() != null ? cart.getItems().size() : 0));
            System.out.println("Cart items: " + cart.getItems());
            
            // Nếu cart trống sau khi xóa item, xóa cart khỏi session
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                System.out.println("Cart is empty, removing from session");
                session.removeAttribute("cartId");
                session.removeAttribute("cart");
                cart = null; // Set cart về null để hiển thị "giỏ hàng trống"
            }
        } else {
            System.out.println("Cart is null from DB!");
            // Nếu cart không tồn tại trong DB, xóa khỏi session
            session.removeAttribute("cartId");
            session.removeAttribute("cart");
        }

        request.setAttribute("cart", cart);

        request.getRequestDispatcher("cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("=== CART SERVLET POST DEBUG ===");
        System.out.println("Action: " + action);

        if ("updateQuantity".equals(action)) {
            try {
                int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                System.out.println("Updating quantity for cart item: " + cartItemId + " to: " + quantity);
                
                cartService.updateCartItemQuantity(cartItemId, quantity);
                System.out.println("Successfully updated quantity");
                
                request.setAttribute("success", "Đã cập nhật số lượng!");
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error updating quantity: " + e.getMessage());
                request.setAttribute("error", e.getMessage());
            }
        }

        // Redirect lại để tránh lỗi submit lại form khi refresh
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
