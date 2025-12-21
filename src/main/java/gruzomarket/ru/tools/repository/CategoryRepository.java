package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentId(Long parentId);

    List<Category> findByNameContainingIgnoreCase(String namePart);
}

