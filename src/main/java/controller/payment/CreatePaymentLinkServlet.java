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
import vn.payos.type.CheckoutResponseData;
import java.io.IOException;
import java.util.Map;

@WebServlet("/create-link")
public class CreatePaymentLinkServlet extends HttpServlet {
    private final OrderService orderService = new OrderServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new IllegalStateException("Phiên làm việc đã hết hạn.");
            }

            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null || cart.getItems().isEmpty()) {
                throw new IllegalStateException("Giỏ hàng rỗng.");
            }

            String name = req.getParameter("recipientName");
            String email = req.getParameter("recipientEmail");
            String phone = req.getParameter("recipientPhone");
            String shippingAddress = req.getParameter("shippingAddress");
            String shippingCity = req.getParameter("shippingCity");
            String shippingDistrict = req.getParameter("shippingDistrict");
            String shippingWard = req.getParameter("shippingWard");
            String note = req.getParameter("note");
            String fullAddress = String.join(", ", shippingAddress, shippingWard, shippingDistrict, shippingCity);

            // TRUYỀN THAM SỐ "PAYOS" VÀO ĐÂY
            Order pendingOrder = orderService.createOrderFromGuestCart(cart, name, email, phone, fullAddress, note, "PayOS");

            if (pendingOrder == null) {
                throw new RuntimeException("Không thể tạo đơn hàng.");
            }

            CheckoutResponseData paymentLinkData = orderService.createPaymentLink(pendingOrder, req);

            // Chỉ xóa giỏ hàng nếu tạo link thành công
            session.removeAttribute("cart");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(paymentLinkData));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}