package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String briefInfo;
    private String description;
    private BigDecimal priceOrigin;
    private BigDecimal priceSale;
    private String imageUrl;
    private String category;
    private int stock; //This is quantity according to table
}
