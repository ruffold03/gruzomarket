package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Brand findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}

