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
        return new BrandDTO(brand.getId(), brand.getName(), brand.getProductCount());
    }

    public Brand toEntity(BrandDTO dto) {
        if (dto == null) {
            return null;
        }
        Brand brand = new Brand();
        // Устанавливаем ID только если он не null и > 0 (для обновления существующего)
        // При создании нового бренда ID должен быть null, чтобы БД сама сгенерировала его
        if (dto.getId() != null && dto.getId() > 0) {
            brand.setId(dto.getId());
        } else {
            // Явно обнуляем ID для нового бренда
            brand.setId(null);
        }
        brand.setName(dto.getName());
        // productCount не устанавливаем при создании/обновлении - это вычисляемое поле
        return brand;
    }
}














