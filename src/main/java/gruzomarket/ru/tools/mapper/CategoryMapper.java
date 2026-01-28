package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.CategoryDTO;
import gruzomarket.ru.tools.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryDTO(category.getId(), category.getName(), parentId, category.getProductCount());
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        // parent устанавливается отдельно через сервис
        return category;
    }
}











