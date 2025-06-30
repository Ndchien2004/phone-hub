package model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "settings")
@Getter
@Setter
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Integer settingId;

    @Column(name = "setting_type", nullable = false, length = 50)
    private String settingType; // 'role', 'category', 'payment_method', 'order_status'

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Relationships where Setting is referenced
    @OneToMany(mappedBy = "role")
    private List<User> users;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    @OneToMany(mappedBy = "paymentMethod")
    private List<Order> ordersByPaymentMethod;

    @OneToMany(mappedBy = "status")
    private List<Order> ordersByStatus;
}