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
                product.getOriginalAuto(),
                product.getIsVisible(),
                product.getImageUrl(),
                product.getImages() != null ? product.getImages().stream()
                        .map(gruzomarket.ru.tools.entity.ProductImage::getImageUrl)
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
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
        product.setIsVisible(dto.getIsVisible() != null ? dto.getIsVisible() : true);
        product.setImageUrl(dto.getImageUrl());

        if (dto.getAdditionalImageUrls() != null) {
            for (String url : dto.getAdditionalImageUrls()) {
                gruzomarket.ru.tools.entity.ProductImage img = new gruzomarket.ru.tools.entity.ProductImage();
                img.setImageUrl(url);
                img.setProduct(product);
                product.getImages().add(img);
            }
        }

        // category устанавливается отдельно через сервис
        return product;
    }
}
