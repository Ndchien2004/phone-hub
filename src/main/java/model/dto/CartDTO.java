package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.entity.CartItem;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private int cartId;
    private Integer userId;
    private long totalMoney;

    private List<CartItemDTO> items;
}
