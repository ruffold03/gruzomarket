package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.ProductDTO;
import gruzomarket.ru.tools.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getArticle(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                categoryId,
                product.getOriginalAuto()
        );
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setArticle(dto.getArticle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setOriginalAuto(dto.getOriginalAuto());
        // category устанавливается отдельно через сервис
        return product;
    }
}






