package model.entity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class Order {
    private Integer orderId;
    private Integer userId;
    private int paymentMethodId;
    private int statusId;
    private String email;
    private String fullName;
    private String address;
    private String note;
    private String phoneNumber;
    private long totalMoney;
    private LocalDateTime orderDate;
    private Integer discount;
    private boolean isDeleted;
    private String transactionCode; // Thêm trường này để lưu mã giao dịch PayOS
    private List<OrderItem> orderItems;
}