package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private int cartItemId;
    private int productId;
    private int quantity;
    private long price;

    private String productName;
    private String image;
    private String color;
    private String memory;
    private long priceSale;
    private long priceOrigin;
}
