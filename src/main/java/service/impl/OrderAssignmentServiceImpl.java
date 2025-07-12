package service.impl;

import model.dao.impl.OrderDAOImpl;
import model.entity.*;
import service.EmailService;
import service.OrderAssignmentService;
import service.assignment.OrderAssignmentAlgorithm;
import service.assignment.impl.RoundRobinAssignmentAlgorithm;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import model.dao.DBContext;

public class OrderAssignmentServiceImpl implements OrderAssignmentService {

    private final OrderDAOImpl orderDAO;
    private final EmailService emailService;
    private final OrderAssignmentAlgorithm assignmentAlgorithm;

    public OrderAssignmentServiceImpl() {
        this.orderDAO = new OrderDAOImpl();
        this.emailService = new EmailServiceImpl();
        this.assignmentAlgorithm = new RoundRobinAssignmentAlgorithm();
    }

    @Override
    public void processOrderAssignments() {
        String processId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            System.out.println("Start the order allocation process: " + processId);

            List<Order> pendingOrders = getPendingOrders();
            if (pendingOrders.isEmpty()) {
                System.out.println("There are no pending orders to assign.");
                return;
            }

            List<Sales> activeSales = getActiveSales();
            if (activeSales.isEmpty()) {
                System.out.println("There are no available sales representatives.");
                return;
            }

            Map<Order, Sales> assignments = assignOrders(pendingOrders, activeSales);

            int assignedCount = 0;
            for (Map.Entry<Order, Sales> entry : assignments.entrySet()) {
                Order order = entry.getKey();
                Sales sales = entry.getValue();

                if (updateOrderStatus(order.getOrderId(), sales.getSalesId())) {
                    saveOrderAssignment(order.getOrderId().toString(), sales.getSalesId(), processId);

                    sendAssignmentNotifications(order, sales);

                    assignedCount++;
                    System.out.println("Order #" + order.getOrderId() + " -> Salesperson " + sales.getSalesId() + " Assignment Completed");
                }
            }

            long executionTime = System.currentTimeMillis() - startTime;

            saveAssignmentLog(processId, pendingOrders.size(), assignedCount, executionTime, true, null);

            System.out.println("Order allocation process completed: " + assignedCount + "/" + pendingOrders.size() + " Assigned");

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            saveAssignmentLog(processId, 0, 0, executionTime, false, e.getMessage());
            System.err.println("Order allocation process error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getPendingOrders() {
        String sql = """
            SELECT o.*, s.name as status_name, pm.name as payment_method_name 
            FROM orders o 
            LEFT JOIN settings s ON o.status_id = s.setting_id 
            LEFT JOIN settings pm ON o.payment_method_id = pm.setting_id 
            WHERE o.is_deleted = 0 
            AND s.setting_type = 'order_status' 
            AND (s.name = 'pending' OR s.description LIKE '%chờ%' OR s.description LIKE '%Chờ%')
            AND (o.assigned_sales_id IS NULL OR o.assigned_sales_id = '')
            ORDER BY o.order_date ASC
            """;

        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId((Integer) rs.getObject("user_id"));
                order.setStatusId(rs.getInt("status_id"));
                order.setPaymentMethodId(rs.getInt("payment_method_id"));
                order.setEmail(rs.getString("email"));
                order.setFullName(rs.getString("full_name"));
                order.setAddress(rs.getString("address"));
                order.setNote(rs.getString("note"));
                order.setPhoneNumber(rs.getString("phone_number"));
                order.setTotalMoney(rs.getLong("total_money"));
                if (rs.getTimestamp("order_date") != null) {
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                }
                order.setDiscount((Integer) rs.getObject("discount"));
                order.setTransactionCode(rs.getString("transaction_code"));
                order.setAssignedSalesId(rs.getString("assigned_sales_id"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Waiting order inquiry error: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Sales> getActiveSales() {
        String sql = """
            SELECT * FROM sales 
            WHERE status = 'Active' 
            AND is_deleted = 0 
            ORDER BY CAST(current_assigned_orders AS INT) ASC
            """;

        List<Sales> salesList = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sales sales = new Sales();
                sales.setSalesId(rs.getString("sales_id"));
                sales.setUserId(rs.getString("user_id"));
                sales.setEmployeeCode(rs.getString("employee_code"));
                sales.setDepartment(rs.getString("department"));
                if (rs.getTimestamp("hire_date") != null) {
                    sales.setHireDate(rs.getTimestamp("hire_date").toLocalDateTime());
                }
                sales.setStatus(rs.getString("status"));
                sales.setMaxOrdersPerDay(rs.getString("max_orders_per_day"));
                sales.setCurrentAssignedOrders(rs.getString("current_assigned_orders"));
                sales.setTotalCompletedOrders(rs.getString("total_completed_orders"));
                sales.setPerformanceRating(rs.getDouble("performance_rating"));
                sales.setIs_deleted(rs.getBoolean("is_deleted"));
                if (rs.getTimestamp("created_at") != null) {
                    sales.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                }
                if (rs.getTimestamp("updated_at") != null) {
                    sales.setUpdated_at(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                salesList.add(sales);
            }
        } catch (SQLException e) {
            System.err.println("Active salesperson lookup error: " + e.getMessage());
            e.printStackTrace();
        }
        return salesList;
    }

    @Override
    public Map<Order, Sales> assignOrders(List<Order> orders, List<Sales> salesList) {
        return assignmentAlgorithm.assignOrders(orders, salesList);
    }

    @Override
    public boolean updateOrderStatus(int orderId, String salesId) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Update order status and assign sales - Fixed for SQL Server
            String updateOrderSql = """
            UPDATE orders
            SET status_id = (
                SELECT TOP 1 setting_id
                FROM settings
                WHERE setting_type = 'ORDER_STATUS'
                AND (name = 'Confirmed' OR description LIKE '%xác nhận%' OR description LIKE '%Xác nhận%')
            ),
            assigned_sales_id = ?
            WHERE order_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                ps.setString(1, salesId);
                ps.setInt(2, orderId);
                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Update sales current assigned orders count
            String updateSalesSql = """
            UPDATE sales
            SET current_assigned_orders = CAST(current_assigned_orders AS INT) + 1
            WHERE sales_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(updateSalesSql)) {
                ps.setString(1, salesId);
                ps.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Transaction rollback error: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Order status update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Connection close error: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void sendAssignmentNotifications(Order order, Sales sales) {
        CompletableFuture.runAsync(() -> {
            try {
                sendCustomerAssignmentEmail(order, sales);

                sendSalesAssignmentEmail(order, sales);

            } catch (Exception e) {
                System.err.println("Error sending allocation notification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void sendCustomerAssignmentEmail(Order order, Sales sales) {
        try {
            String subject = "Order confirmation and assigning a representative - Order #" + order.getOrderId();
            String content = buildCustomerAssignmentEmailContent(order, sales);

            System.out.println("Send customer allocation notification email: " + order.getEmail());

        } catch (Exception e) {
            System.err.println("Error sending customer assignment email: " + e.getMessage());
        }
    }

    private void sendSalesAssignmentEmail(Order order, Sales sales) {
        try {
            String salesEmail = getSalesEmail(sales.getSalesId());
            if (salesEmail != null) {
                String subject = "New Order Assignment Notification - Order #" + order.getOrderId();
                String content = buildSalesAssignmentEmailContent(order, sales);

                System.out.println("Send sales rep assignment notification email: " + salesEmail);
            }

        } catch (Exception e) {
            System.err.println("Error sending email to sales representative: " + e.getMessage());
        }
    }

    private String getSalesEmail(String salesId) {
        String sql = """
            SELECT u.email 
            FROM sales s 
            JOIN users u ON s.user_id = u.user_id 
            WHERE s.sales_id = ? AND s.is_deleted = 0
            """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, salesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            System.err.println("Salesperson email inquiry error: " + e.getMessage());
        }
        return null;
    }

    private void saveOrderAssignment(String orderId, String salesId, String processId) {
        String sql = """
            INSERT INTO order_assignments (order_id, sales_id, assigned_at, assigned_by, assignment_algorithm, is_active, is_deleted)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, orderId);
            ps.setString(2, salesId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, "SYSTEM");
            ps.setString(5, assignmentAlgorithm.getAlgorithmName());
            ps.setBoolean(6, true);
            ps.setBoolean(7, false);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving order allocation record: " + e.getMessage());
        }
    }

    private void saveAssignmentLog(String processId, int totalProcessed, int totalAssigned, long executionTime, boolean success, String errorMessage) {
        String sql = """
            INSERT INTO assignment_logs (process_id, total_orders_processed, total_orders_assigned, assignment_algorithm, execution_time_ms, success, error_message, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, processId);
            ps.setString(2, String.valueOf(totalProcessed));
            ps.setString(3, String.valueOf(totalAssigned));
            ps.setString(4, assignmentAlgorithm.getAlgorithmName());
            ps.setString(5, String.valueOf(executionTime));
            ps.setBoolean(6, success);
            ps.setString(7, errorMessage);
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving allocation log: " + e.getMessage());
        }
    }

    private String buildCustomerAssignmentEmailContent(Order order, Sales sales) {
        return String.format(
                "<html><body>" +
                        "<h2>Order confirmation and assigning a person in charge</h2>" +
                        "<p>Hello %s,</p>" +
                        "<p>Order #%d has been confirmed and assigned to a responsible person.</p>" +
                        "<p><strong>Sales Representative in Charge:</strong> %s</p>" +
                        "<p><strong>Employee Code:</strong> %s</p>" +
                        "<p>A representative will contact you shortly.</p>" +
                        "<p>Thank you.<br>PhoneHub 팀</p>" +
                        "</body></html>",
                order.getFullName(),
                order.getOrderId(),
                sales.getEmployeeCode(),
                sales.getEmployeeCode()
        );
    }

    private String buildSalesAssignmentEmailContent(Order order, Sales sales) {
        return String.format(
                "<html><body>" +
                        "<h2>New Order Assignment Notification</h2>" +
                        "<p>A new order has been assigned.</p>" +
                        "<p><strong>Order Number:</strong> #%d</p>" +
                        "<p><strong>Customer name:</strong> %s</p>" +
                        "<p><strong>Contact:</strong> %s</p>" +
                        "<p><strong>Order amount:</strong> %,d원</p>" +
                        "<p><strong>Order date:</strong> %s</p>" +
                        "<p>Please see the admin page for more details.</p>" +
                        "<p>PhoneHub 시스템</p>" +
                        "</body></html>",
                order.getOrderId(),
                order.getFullName(),
                order.getPhoneNumber(),
                order.getTotalMoney(),
                order.getFormattedOrderDate()
        );
    }
}