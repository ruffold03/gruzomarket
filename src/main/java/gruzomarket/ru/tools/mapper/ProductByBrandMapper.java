package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.ProductByBrandDTO;
import gruzomarket.ru.tools.entity.ProductByBrand;
import org.springframework.stereotype.Component;

@Component
public class ProductByBrandMapper {

    public ProductByBrandDTO toDTO(ProductByBrand productByBrand) {
        if (productByBrand == null || productByBrand.getId() == null) {
            return null;
        }
        return new ProductByBrandDTO(
                productByBrand.getId().getProductId(),
                productByBrand.getId().getBrandId()
        );
    }

    public ProductByBrand toEntity(ProductByBrandDTO dto) {
        if (dto == null) {
            return null;
        }
        ProductByBrand productByBrand = new ProductByBrand();
        ProductByBrand.ProductBrandId id = new ProductByBrand.ProductBrandId();
        id.setProductId(dto.getProductId());
        id.setBrandId(dto.getBrandId());
        productByBrand.setId(id);
        // product и brand устанавливаются отдельно через сервис
        return productByBrand;
    }
}











