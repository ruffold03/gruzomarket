package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByArticle(String article);

    List<Product> findByNameContainingIgnoreCase(String namePart);

    List<Product> findByCategoryId(Long categoryId);

    Page<Product> findByCategoryIdIn(List<Long> categoryIds, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(LOWER(p.article) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(p.category.id IN :categoryIds) AND " +
           "(p.price BETWEEN :minPrice AND :maxPrice) AND " +
           "(p.quantity > 0) AND " +
           "((:brandId) IS NULL OR p.id IN (SELECT p2.id FROM Product p2 JOIN p2.productLinks pb WHERE pb.brand.id = :brandId))")
    Page<Product> findWithFilters(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("brandId") Long brandId,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.originalAuto) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(p.quantity > 0)")
    List<Product> findByTruckModel(@Param("model") String model);

    @Query("SELECT DISTINCT p FROM Product p " +
           "JOIN p.productLinks pb " +
           "WHERE pb.brand.id = :brandId")
    Page<Product> findByBrandId(@Param("brandId") Long brandId, Pageable pageable);
}

