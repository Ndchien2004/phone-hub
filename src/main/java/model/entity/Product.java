package model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_name", "color", "memory"})
})
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Setting category;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "image", columnDefinition = "NVARCHAR(MAX)")
    private String image;

    @Column(name = "brief_info", columnDefinition = "NVARCHAR(MAX)")
    private String briefInfo;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @Column(name = "memory", nullable = false, length = 50)
    private String memory;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_sale", nullable = false)
    private Long priceSale;

    @Column(name = "price_origin", nullable = false)
    private Long priceOrigin;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}