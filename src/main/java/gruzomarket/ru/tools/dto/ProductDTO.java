package gruzomarket.ru.tools.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private Long id;
    private String name;
    private String article;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
    private String originalAuto;
    private Boolean isVisible;
}














