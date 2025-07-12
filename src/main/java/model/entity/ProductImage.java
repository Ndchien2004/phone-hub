package model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImage {
    private Integer imageId;
    private Integer productId; // Lưu ID trực tiếp, không phải đối tượng Product
    private String imageUrl;
    private boolean isDeleted = false;
}