package gruzomarket.ru.tools.dto;

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
    private String article;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
    private String originalAuto;
}






