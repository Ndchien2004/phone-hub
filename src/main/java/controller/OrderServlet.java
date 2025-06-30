
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.OrderDAO;
import model.entity.Order;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/orders", "/orders/*"})
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

    private void showAllOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Lấy danh sách orders từ database
            List<Order> orders = orderDAO.getAllOrders();

            // Set attribute để truyền dữ liệu sang JSP
            req.setAttribute("orders", orders);
            req.setAttribute("totalOrders", orders.size());

            // Forward request đến JSP
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
                // Set attribute để truyền dữ liệu sang JSP
                req.setAttribute("order", order);

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