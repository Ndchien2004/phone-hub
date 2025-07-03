package controller.payment;

import com.google.gson.Gson;
import model.entity.Cart;
import model.entity.Order;
import service.OrderService;
import service.impl.OrderServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/create-vnpay-link")
public class CreateVnpayLinkServlet extends HttpServlet {
    private final OrderService orderService = new OrderServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = req.getSession(false);
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null || cart.getItems().isEmpty()) {
                throw new IllegalStateException("Giỏ hàng rỗng.");
            }

            String name = req.getParameter("recipientName");
            String email = req.getParameter("recipientEmail");
            String phone = req.getParameter("recipientPhone");
            String address = String.join(", ", req.getParameter("shippingAddress"), req.getParameter("shippingWard"), req.getParameter("shippingDistrict"), req.getParameter("shippingCity"));
            String note = req.getParameter("note");

            // Tạo đơn hàng với phương thức là VNPAY
            Order pendingOrder = orderService.createOrderFromGuestCart(cart, name, email, phone, address, note, "VNPAY");
            if (pendingOrder == null) {
                throw new RuntimeException("Không thể tạo đơn hàng.");
            }

            // Gọi service để tạo URL thanh toán
            String paymentUrl = orderService.createVnpayPaymentUrl(pendingOrder, req);

            // Xóa giỏ hàng sau khi đã tạo link thành công
            session.removeAttribute("cart");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of("paymentUrl", paymentUrl)));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}