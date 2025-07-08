package controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.impl.OrderDAOImpl;
import model.entity.Order;
import model.entity.OrderItem;

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
            if ("cancel".equals(action)) {
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
}