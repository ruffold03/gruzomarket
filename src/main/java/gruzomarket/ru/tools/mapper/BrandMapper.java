package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.BrandDTO;
import gruzomarket.ru.tools.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public BrandDTO toDTO(Brand brand) {
        if (brand == null) {
            return null;
        }
        return new BrandDTO(brand.getId(), brand.getName());
    }

    public Brand toEntity(BrandDTO dto) {
        if (dto == null) {
            return null;
        }
        Brand brand = new Brand();
        brand.setId(dto.getId());
        brand.setName(dto.getName());
        return brand;
    }
}











