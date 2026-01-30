package gruzomarket.ru.tools.repository.spec;

import gruzomarket.ru.tools.entity.Product;
import gruzomarket.ru.tools.entity.ProductByBrand;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> textQuery(String q) {
        return (root, query, cb) -> {
            if (q == null || q.trim().isEmpty()) {
                return cb.conjunction();
            }
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(root.get("article")), like),
                    cb.like(cb.lower(root.get("originalAuto")), like)
            );
        };
    }

    public static Specification<Product> categoryIds(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("category").get("id").in(categoryIds);
        };
    }

    public static Specification<Product> brandIds(List<Long> brandIds) {
        return (root, query, cb) -> {
            if (brandIds == null || brandIds.isEmpty()) {
                return cb.conjunction();
            }
            query.distinct(true);
            Join<Product, ProductByBrand> link = root.join("productLinks", JoinType.INNER);
            return link.get("brand").get("id").in(brandIds);
        };
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();
            if (minPrice != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return p;
        };
    }

    public static Specification<Product> inStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null) {
                return cb.conjunction();
            }
            if (Boolean.TRUE.equals(inStock)) {
                return cb.greaterThan(root.get("quantity"), 0);
            }
            return cb.lessThanOrEqualTo(root.get("quantity"), 0);
        };
    }

    public static Specification<Product> isVisible(Boolean visible) {
        return (root, query, cb) -> {
            if (visible == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("isVisible"), visible);
        };
    }
}





