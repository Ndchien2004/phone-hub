package controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.impl.OrderDAOImpl;
import model.entity.*;
import service.LocationService;
import service.impl.LocationServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/orders", "/orders/*"})
public class OrderServlet extends HttpServlet {

    private OrderDAOImpl orderDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDAO = new OrderDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String pathInfo = req.getPathInfo();

        System.out.println("OrderServlet - doGet called with path: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                System.out.println("Fetching all orders");
                showAllOrders(req, resp);
            } else if (pathInfo == null || pathInfo.equals("/change-address")) {
                changeOrderAddress( req, resp);
            } else {
                // Handle specific order details by ID in URL path
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1 && !pathParts[1].isEmpty()) {
                    try {
                        int orderId = Integer.parseInt(pathParts[1]);
                        System.out.println("Fetching order details for ID: " + orderId);
                        showOrderDetails(req, resp, orderId);
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
                    }
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID is required");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String action = req.getParameter("action");

        System.out.println("OrderServlet - doPost called with path: " + pathInfo + ", action: " + action);

        try {
            if (pathInfo != null && pathInfo.equals("/change-address")) {
                handleUpdateAddress(req, resp);
            } else if ("cancel".equals(action)) {
                handleCancelOrder(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request: " + e.getMessage());
        }
    }

    private void handleCancelOrder(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String orderIdParam = req.getParameter("orderId");
            if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
                sendJsonResponse(resp, false, "Order ID is required");
                return;
            }

            int orderId = Integer.parseInt(orderIdParam);

            // Kiểm tra xem đơn hàng có thể hủy được không
            if (!orderDAO.canCancelOrder(orderId)) {
                sendJsonResponse(resp, false, "Đơn hàng này không thể hủy được. Chỉ có thể hủy đơn hàng đang chờ xử lý hoặc đang xử lý.");
                return;
            }

            // Thực hiện hủy đơn hàng
            boolean success = orderDAO.cancelOrder(orderId);

            if (success) {
                sendJsonResponse(resp, true, "Đơn hàng đã được hủy thành công!");
            } else {
                sendJsonResponse(resp, false, "Không thể hủy đơn hàng. Vui lòng thử lại sau.");
            }

        } catch (NumberFormatException e) {
            sendJsonResponse(resp, false, "Order ID không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(resp, false, "Đã xảy ra lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, boolean success, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        String jsonResponse = String.format(
                "{\"success\": %s, \"message\": \"%s\"}",
                success,
                message.replace("\"", "\\\"")
        );
        out.print(jsonResponse);
        out.flush();
    }

    private void showAllOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Get all orders from database
            List<Order> orders = orderDAO.getAllOrders();

            // Fetch additional details for each order
            for (Order order : orders) {
                // Get order items for each order
                List<OrderItem> orderItems = orderDAO.getOrderItemsByOrderId(order.getOrderId());

                // Make sure each order item has its product populated
                for (OrderItem item : orderItems) {
                    if (item.getProduct() == null) {
                        // You need to add this method to your OrderDAO or use ProductDAO
                        // Product product = productDAO.getProductById(item.getProductId());
                        // item.setProduct(product);
                    }
                }

                order.setOrderItems(orderItems);
            }

            // Set attributes for JSP
            req.setAttribute("orders", orders);
            req.setAttribute("totalOrders", orders.size());

            // Forward to JSP
            req.getRequestDispatcher("/order-list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading orders: " + e.getMessage());
        }
    }

    private void showOrderDetails(HttpServletRequest req, HttpServletResponse resp, int orderId) throws ServletException, IOException {
        try {
            // Lấy thông tin chi tiết đơn hàng từ database
            Order order = orderDAO.getOrderById(orderId);

            if (order != null) {
                // Lấy danh sách sản phẩm trong đơn hàng
                List<OrderItem> orderItems = orderDAO.getOrderItemsByOrderId(orderId);

                // Kiểm tra xem đơn hàng có thể hủy được không
                boolean canCancel = orderDAO.canCancelOrder(orderId);

                // Set attribute để truyền dữ liệu sang JSP
                req.setAttribute("order", order);
                req.setAttribute("orderItems", orderItems);
                req.setAttribute("totalItems", orderItems.size());
                req.setAttribute("canCancel", canCancel);

                // Tính tổng giá trị đơn hàng từ các order items
                long totalAmount = orderItems.stream()
                        .mapToLong(item -> item.getPrice() * item.getQuantity())
                        .sum();
                req.setAttribute("totalAmount", totalAmount);

                // Forward request đến JSP chi tiết đơn hàng
                req.getRequestDispatcher("/order-detail.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found with ID: " + orderId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading order details: " + e.getMessage());
        }
    }

    private void changeOrderAddress(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderId = req.getParameter("orderId");
        OrderDAOImpl orderDAO = new OrderDAOImpl();
        LocationService locationService = new LocationServiceImpl();

        if (orderId == null || orderId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        try {
            // Get order details from database
            Order order = orderDAO.getOrderById(Integer.parseInt(orderId));

            if (order == null) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }

            // Check if order can be modified (only pending or processing orders)
            if (!orderDAO.canModifyOrder(Integer.parseInt(orderId))) {
                req.setAttribute("error", "Không thể thay đổi địa chỉ cho đơn hàng này. Chỉ có thể thay đổi đơn hàng đang chờ xử lý hoặc đang xử lý.");
                req.setAttribute("order", order);
                req.getRequestDispatcher("/change-address.jsp").forward(req, resp);
                return;
            }

            // Load provinces for dropdown (you'll need to implement this)
            List<Province> provinces = locationService.getAllProvinces();
            List<District> districts = locationService.getAllDistricts();
            List<Ward> wards = locationService.getAllWards();
            req.setAttribute("provinces", provinces);
            req.setAttribute("districts", districts);
            req.setAttribute("wards", wards);

            // Set order data for the form
            req.setAttribute("order", order);

            req.getRequestDispatcher("/change-address.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/orders");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/orders");
        }
    }

    private void handleUpdateAddress(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String orderId = req.getParameter("orderId");
            String recipientName = req.getParameter("recipientName");
            String recipientPhone = req.getParameter("recipientPhone");
            String recipientEmail = req.getParameter("recipientEmail");
            String shippingAddress = req.getParameter("shippingAddress");
            String shippingCity = req.getParameter("shippingCity");
            String shippingDistrict = req.getParameter("shippingDistrict");
            String shippingWard = req.getParameter("shippingWard");
            String note = req.getParameter("note");

            // Validate required fields
            if (orderId == null || recipientName == null || recipientPhone == null ||
                    recipientEmail == null || shippingAddress == null || shippingCity == null ||
                    shippingDistrict == null || shippingWard == null) {
                req.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc");
                changeOrderAddress(req, resp);
                return;
            }

            // Validate input format
            if (!isValidPhone(recipientPhone)) {
                req.setAttribute("errorMessage", "Số điện thoại không hợp lệ");
                changeOrderAddress(req, resp);
                return;
            }

            if (!isValidEmail(recipientEmail)) {
                req.setAttribute("errorMessage", "Email không hợp lệ");
                changeOrderAddress(req, resp);
                return;
            }

            // Combine full address
            String fullAddress = shippingAddress + ", " + shippingWard + ", " + shippingDistrict + ", " + shippingCity;

            // Check if order can be modified
            if (!orderDAO.canModifyOrder(Integer.parseInt(orderId))) {
                req.setAttribute("errorMessage", "Không thể thay đổi địa chỉ cho đơn hàng này");
                changeOrderAddress(req, resp);
                return;
            }

            // Update order address in database
            boolean updated = orderDAO.updateOrderAddress(
                    Integer.parseInt(orderId),
                    recipientName,
                    recipientPhone,
                    recipientEmail,
                    fullAddress,
                    note
            );

            if (updated) {
                // Redirect to order detail page with success message
                resp.sendRedirect(req.getContextPath() + "/orders/" + orderId + "?updateSuccess=true");
            } else {
                req.setAttribute("errorMessage", "Không thể cập nhật địa chỉ. Vui lòng thử lại");
                changeOrderAddress(req, resp);
            }

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/orders");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật địa chỉ");
            changeOrderAddress(req, resp);
        }
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^0\\d{9}$");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
}