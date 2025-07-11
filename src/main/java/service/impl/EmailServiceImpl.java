// THAY ĐỔI: Gói (package) của bạn có thể là service.impl hoặc service.util, hãy đảm bảo nó đúng
package service.impl; // Hoặc package service.util tùy cấu trúc của bạn

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import model.entity.Order;
import model.entity.OrderItem; // Import OrderItem
import model.entity.Product;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

// Sửa tên file cho đúng với interface của bạn
public class EmailServiceImpl implements service.EmailService {

    private final Properties properties = new Properties();

    public EmailServiceImpl() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("mail.properties")) {
            if (input == null) {
                System.err.println("Lỗi nghiêm trọng: không tìm thấy file mail.properties");
                return;
            }
            properties.load(input);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải file mail.properties: " + e.getMessage());
        }
    }

    @Override
    public void sendOrderConfirmationEmail(Order order, String recipientEmail) {
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Xác nhận đơn hàng #" + order.getOrderId() + " từ PhoneHub");

            String htmlContent = getEmailContent(order);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            new Thread(() -> {
                try {
                    Transport.send(message);
                    System.out.println("Đã gửi email xác nhận đơn hàng tới " + recipientEmail);
                } catch (MessagingException e) {
                    System.err.println("Lỗi khi gửi email: " + e.getMessage());
                }
            }).start();

        } catch (MessagingException e) {
            System.err.println("Lỗi khi tạo message email: " + e.getMessage());
        }
    }

    @Override
    public boolean sendOrderCancellationEmail(Order order) {
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        properties.getProperty("mail.smtp.user"),
                        properties.getProperty("mail.smtp.password")
                );
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getEmail()));
            System.out.println("Địa chỉ email người nhận: " + order.getEmail());
            message.setSubject("Thông báo hủy đơn hàng #" + order.getOrderId() + " từ PhoneHub");

            String htmlContent = getCancellationEmailContent(order);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Đã gửi email thông báo hủy đơn hàng tới " + order.getEmail());
            return true;

        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email hủy đơn hàng: " + e.getMessage());
            return false;
        }
    }

    private String getCancellationEmailContent(Order order) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/order_cancellation_template.html")) {
            if (is == null) {
                return getDefaultCancellationEmailContent(order);
            }

            String template = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            Locale vietnameseLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnameseLocale);

            // Add null checks for all replacements
            template = template.replace("{{customerName}}", order.getFullName() != null ? order.getFullName() : "Khách hàng");
            template = template.replace("{{orderId}}", String.valueOf(order.getOrderId()));
            template = template.replace("{{orderDate}}", order.getFormattedOrderDate() != null ? order.getFormattedOrderDate() : "Không xác định");
            template = template.replace("{{totalAmount}}", currencyFormatter.format(order.getTotalMoney()));
            template = template.replace("{{currentDate}}", java.time.LocalDate.now().toString());

            // Build items table for cancelled order with null checks
            StringBuilder itemsTable = new StringBuilder();
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    if (product != null && product.getProductName() != null) {
                        itemsTable.append("<tr>")
                                .append("<td style='padding: 8px; border: 1px solid #ddd;'>").append(product.getProductName()).append("</td>")
                                .append("<td style='padding: 8px; border: 1px solid #ddd; text-align: center;'>").append(item.getQuantity()).append("</td>")
                                .append("<td style='padding: 8px; border: 1px solid #ddd; text-align: right;'>").append(currencyFormatter.format(item.getPrice())).append("</td>")
                                .append("</tr>");
                    }
                }
            }
            template = template.replace("{{itemsTable}}", itemsTable.toString());

            return template;

        } catch (Exception e) {
            System.err.println("Lỗi khi đọc template email hủy đơn hàng: " + e.getMessage());
            return getDefaultCancellationEmailContent(order);
        }
    }

    private String getDefaultCancellationEmailContent(Order order) {
        Locale vietnameseLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnameseLocale);

        String customerName = order.getFullName() != null ? order.getFullName() : "Khách hàng";

        return String.format(
                "<html><body>" +
                        "<h2>Thông báo hủy đơn hàng</h2>" +
                        "<p>Xin chào %s,</p>" +
                        "<p>Đơn hàng #%d của bạn đã được hủy thành công.</p>" +
                        "<p>Tổng tiền: %s</p>" +
                        "<p>Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.</p>" +
                        "<p>Trân trọng,<br>Đội ngũ PhoneHub</p>" +
                        "</body></html>",
                customerName,
                order.getOrderId(),
                currencyFormatter.format(order.getTotalMoney())
        );
    }

    @Override
    public boolean sendAddressChangeEmail(Order order, String oldAddress, String newAddress) {
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        properties.getProperty("mail.smtp.user"),
                        properties.getProperty("mail.smtp.password")
                );
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getEmail()));
            message.setSubject("Thông báo thay đổi địa chỉ giao hàng - Đơn hàng #" + order.getOrderId() + " từ PhoneHub");

            String htmlContent = getAddressChangeEmailContent(order, oldAddress, newAddress);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Đã gửi email thông báo thay đổi địa chỉ tới " + order.getEmail());
            return true;

        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email thay đổi địa chỉ: " + e.getMessage());
            return false;
        }
    }

    private String getAddressChangeEmailContent(Order order, String oldAddress, String newAddress) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/address_change_template.html")) {
            if (is == null) {
                return getDefaultAddressChangeEmailContent(order, oldAddress, newAddress);
            }

            String template = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            Locale vietnameseLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnameseLocale);

            // Add null checks for all replacements
            template = template.replace("{{customerName}}", order.getFullName() != null ? order.getFullName() : "Khách hàng");
            template = template.replace("{{orderId}}", String.valueOf(order.getOrderId()));
            template = template.replace("{{orderDate}}", order.getFormattedOrderDate() != null ? order.getFormattedOrderDate() : "Không xác định");
            template = template.replace("{{totalAmount}}", currencyFormatter.format(order.getTotalMoney()));
            template = template.replace("{{currentDate}}", java.time.LocalDate.now().toString());
            template = template.replace("{{oldAddress}}", oldAddress != null ? oldAddress : "Không xác định");
            template = template.replace("{{newAddress}}", newAddress != null ? newAddress : "Không xác định");

            return template;

        } catch (Exception e) {
            System.err.println("Lỗi khi đọc template email thay đổi địa chỉ: " + e.getMessage());
            return getDefaultAddressChangeEmailContent(order, oldAddress, newAddress);
        }
    }

    private String getDefaultAddressChangeEmailContent(Order order, String oldAddress, String newAddress) {
        Locale vietnameseLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnameseLocale);

        String customerName = order.getFullName() != null ? order.getFullName() : "Khách hàng";

        return String.format(
                "<html><body>" +
                        "<h2>Thông báo thay đổi địa chỉ giao hàng</h2>" +
                        "<p>Xin chào %s,</p>" +
                        "<p>Địa chỉ giao hàng cho đơn hàng #%d của bạn đã được thay đổi thành công.</p>" +
                        "<p><strong>Địa chỉ cũ:</strong> %s</p>" +
                        "<p><strong>Địa chỉ mới:</strong> %s</p>" +
                        "<p>Tổng tiền: %s</p>" +
                        "<p>Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.</p>" +
                        "<p>Trân trọng,<br>Đội ngũ PhoneHub</p>" +
                        "</body></html>",
                customerName,
                order.getOrderId(),
                oldAddress != null ? oldAddress : "Không xác định",
                newAddress != null ? newAddress : "Không xác định",
                currencyFormatter.format(order.getTotalMoney())
        );
    }

    private String getEmailContent(Order order) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/order_confirmation_template.html")) {
            if (is == null) {
                return "Lỗi: Không tìm thấy template email.";
            }

            String template = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            // SỬA LỖI: Dùng cách khởi tạo Locale mới không bị deprecated
            Locale vietnameseLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnameseLocale);

            // SỬA LỖI: Sử dụng đúng tên phương thức từ Order.java của bạn
            template = template.replace("{{customerName}}", order.getFullName());
            template = template.replace("{{orderId}}", String.valueOf(order.getOrderId()));
            template = template.replace("{{orderDate}}", order.getFormattedOrderDate()); // Dùng hàm đã có sẵn
            template = template.replace("{{shippingAddress}}", order.getAddress());
            String paymentMethodName = (order.getPaymentMethod() != null) ? order.getPaymentMethod().getName() : "Chưa xác định";
            template = template.replace("{{paymentMethod}}", paymentMethodName);
            template = template.replace("{{totalAmount}}", currencyFormatter.format(order.getTotalMoney()));

            // SỬA LỖI: Lấy danh sách sản phẩm từ getOrderItems() thay vì getCart()
            StringBuilder itemsTable = new StringBuilder();
            for (OrderItem item : order.getOrderItems()) {
                // Lấy đối tượng Product từ OrderItem
                Product product = item.getProduct();
                if (product != null) { // Kiểm tra để đảm bảo product không bị null
                    itemsTable.append("<tr>")
                            // Sửa lại cách lấy tên sản phẩm
                            .append("<td style='padding: 8px; border: 1px solid #ddd;'>").append(product.getProductName()).append("</td>")
                            .append("<td style='padding: 8px; border: 1px solid #ddd; text-align: center;'>").append(item.getQuantity()).append("</td>")
                            .append("<td style='padding: 8px; border: 1px solid #ddd; text-align: right;'>").append(currencyFormatter.format(item.getPrice())).append("</td>")
                            .append("</tr>");
                }
            }
            template = template.replace("{{itemsTable}}", itemsTable.toString());

            return template;

        } catch (Exception e) {
            System.err.println("Lỗi khi đọc template email: " + e.getMessage());
            return "Đơn hàng của bạn đã được xác nhận. Chúng tôi sẽ liên hệ sớm.";
        }
    }
}