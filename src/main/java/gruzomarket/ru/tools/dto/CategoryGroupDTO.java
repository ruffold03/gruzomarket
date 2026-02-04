package gruzomarket.ru.tools.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryGroupDTO {
    private String groupName;
    private List<CategoryDTO> categories;
}