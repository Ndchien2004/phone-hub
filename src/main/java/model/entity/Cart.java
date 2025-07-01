package model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Cart {
    private int cartId;
    private Integer userId;
    private LocalDateTime createdAt;
    private long totalMoney = 0;
    private boolean isDeleted = false;
    private List<CartItem> items = new ArrayList<>();

    // CÁC THUỘC TÍNH MỚI ĐƯỢC BỔ SUNG ĐỂ PHÙ HỢP VỚI JSP
    private long rawTotal;
    private long shippingFee = 0; // Tạm thời để là 0
    private long discount = 0; // Tạm thời để là 0
    private long finalTotal;

    public void addItem(CartItem item) {
        for (CartItem existingItem : items) {
            if (existingItem.getProductId() == item.getProductId()) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                calculateTotals(); // Cập nhật lại tổng tiền
                return;
            }
        }
        items.add(item);
        calculateTotals(); // Cập nhật lại tổng tiền
    }

    // PHƯƠNG THỨC MỚI ĐỂ TÍNH TOÁN TẤT CẢ CÁC LOẠI TỔNG TIỀN
    public void calculateTotals() {
        this.rawTotal = 0;
        for (CartItem item : items) {
            this.rawTotal += item.getPrice() * item.getQuantity();
        }
        // Logic tính toán phức tạp hơn có thể được thêm vào đây
        this.finalTotal = this.rawTotal + this.shippingFee - this.discount;

        // Gán cho totalMoney để giữ tương thích với code cũ nếu có
        this.totalMoney = this.finalTotal;
    }
}