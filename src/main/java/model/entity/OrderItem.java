package model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Long price;

    // Temporary fields for display purposes (not persisted)
    @Transient
    private String productName;

    @Transient
    private String productImageUrl;

    @Transient
    private String productBriefInfo;

    @Transient
    private String productDescription;

    @Transient
    private String productColor;

    @Transient
    private String productMemory;

    @Transient
    private Long productPriceSale;

    @Transient
    private Long productPriceOrigin;

    // Helper method để tính tổng tiền cho item này
    public Long getTotalPrice() {
        return price * quantity;
    }

    // Helper method để format giá tiền
    public String getFormattedPrice() {
        return String.format("%,d VNĐ", price);
    }

    public String getFormattedTotalPrice() {
        return String.format("%,d VNĐ", getTotalPrice());
    }
}