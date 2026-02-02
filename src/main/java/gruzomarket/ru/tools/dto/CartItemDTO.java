package gruzomarket.ru.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long productId;
    private String name;
    private String article;
    private BigDecimal unitPrice;
    private Integer quantityAvailable;
    private Integer quantity;
    private BigDecimal lineTotal;
    private Long categoryId;
}







