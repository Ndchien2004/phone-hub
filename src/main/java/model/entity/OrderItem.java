package model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItem {
    private Integer orderItemId;
    private Integer orderId;
    //    private Integer productId; // không cần nữa, vì đã có đối tượng Product rồi
    private int quantity;
    private long price;
    private boolean isDeleted = false;

    private Product product; // Giữ lại để tiện truy cập thông tin sản phẩm

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