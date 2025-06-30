package model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private Setting paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Setting status;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "address", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String address;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "total_money")
    private Long totalMoney;

    @Column(name = "order_date", updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
}