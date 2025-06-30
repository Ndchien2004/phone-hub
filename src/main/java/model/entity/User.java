package model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Setting role;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "image", columnDefinition = "NVARCHAR(MAX)")
    private String image;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "google_id", length = 255)
    private String googleId;

    @Column(name = "address", columnDefinition = "NVARCHAR(MAX)")
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "reset_password_token", columnDefinition = "NVARCHAR(MAX)")
    private String resetPasswordToken;

    @Column(name = "reset_password_expiry")
    private LocalDateTime resetPasswordExpiry;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}