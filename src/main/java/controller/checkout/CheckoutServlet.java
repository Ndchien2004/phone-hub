package controller.checkout;

import model.entity.Cart;
import model.entity.Order;
import model.entity.District;
import model.entity.Province;
import model.entity.Ward;
import service.EmailService;
import service.LocationService;
import service.OrderService;
import service.impl.EmailServiceImpl;
import service.impl.LocationServiceImpl;
import service.impl.OrderServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private final LocationService locationService = new LocationServiceImpl();
    private final OrderService orderService = new OrderServiceImpl();
    private final EmailService emailService = new EmailServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("--- [CheckoutServlet] Bắt đầu xử lý GET. ---");
        try {
            List<Province> provinces = locationService.getAllProvinces();
            List<District> districts = locationService.getAllDistricts();
            List<Ward> wards = locationService.getAllWards();
            req.setAttribute("provinces", provinces);
            req.setAttribute("districts", districts);
            req.setAttribute("wards", wards);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi tải dữ liệu địa chỉ.");
        }
        req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        System.out.println("--- [CheckoutServlet] Bắt đầu xử lý POST (đặt hàng COD) ---");

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("checkout?error=session_expired");
            return;
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            req.setAttribute("errorMessage", "Giỏ hàng của bạn đang rỗng, không thể đặt hàng.");
            doGet(req, resp);
            return;
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

        String sendPromoEmail = req.getParameter("promo-email");

        try {
            // TRUYỀN THAM SỐ "COD" VÀO ĐÂY
            Order order = orderService.createOrderFromGuestCart(cart, name, email, phone, fullAddress, note, "COD");

            if (order == null) {
                req.setAttribute("errorMessage", "Đã có lỗi xảy ra khi tạo đơn hàng. Vui lòng thử lại.");
                doGet(req, resp);
                return;
            }

            System.out.println("Đã tạo đơn hàng COD thành công với ID: " + order.getOrderId());
            // === LOGIC GỬI MAIL ===
            // Chỉ gửi mail nếu người dùng đã check vào ô đồng ý
            if (sendPromoEmail != null && sendPromoEmail.equals("on")) {
                try {
                    // Gọi service để gửi mail, truyền vào đối tượng order và email người nhận
                    emailService.sendOrderConfirmationEmail(order, email);
                } catch (Exception e) {
                    // Nếu gửi mail lỗi, chỉ in ra log và không làm ảnh hưởng đến luồng chính
                    System.err.println("Gửi mail xác nhận cho đơn hàng " + order.getOrderId() + " thất bại.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Người dùng không chọn nhận email xác nhận.");
            }
            // ========================

            session.removeAttribute("cart");
            req.setAttribute("order", order);
            req.getRequestDispatcher("/order-confirmation.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi hệ thống không mong muốn: " + e.getMessage());
            doGet(req, resp);
        }
    }
}