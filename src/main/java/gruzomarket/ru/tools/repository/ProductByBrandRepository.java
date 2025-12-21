package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.ProductByBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductByBrandRepository extends JpaRepository<ProductByBrand, ProductByBrand.ProductBrandId> {

    List<ProductByBrand> findByBrandId(Long brandId);

    List<ProductByBrand> findByProductId(Long productId);
}

