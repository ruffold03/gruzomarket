package gruzomarket.ru.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDTO {
    private List<CartItemDTO> items;
    private int totalItems;
    private BigDecimal totalAmount;
}






