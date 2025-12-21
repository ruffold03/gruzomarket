package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByArticle(String article);

    List<Product> findByNameContainingIgnoreCase(String namePart);

    List<Product> findByCategoryId(Long categoryId);
}

