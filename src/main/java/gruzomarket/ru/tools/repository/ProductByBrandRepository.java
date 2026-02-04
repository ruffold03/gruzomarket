package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.ProductByBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductByBrandRepository extends JpaRepository<ProductByBrand, ProductByBrand.ProductBrandId> {

    List<ProductByBrand> findByBrandId(Long brandId);

    List<ProductByBrand> findByProductId(Long productId);

    // Добавьте этот метод:
    @Query("SELECT COUNT(DISTINCT pb.product.id) FROM ProductByBrand pb WHERE pb.brand.id = :brandId")
    Long countProductsByBrandId(@Param("brandId") Long brandId);
}

