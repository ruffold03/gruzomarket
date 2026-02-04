package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.CategoryDTO;
import gruzomarket.ru.tools.dto.CategoryGroupDTO;
import gruzomarket.ru.tools.dto.ProductDTO;
import gruzomarket.ru.tools.dto.ProductSearchResponse;
import gruzomarket.ru.tools.entity.Brand;
import gruzomarket.ru.tools.service.BrandService;
import gruzomarket.ru.tools.service.CategoryService;
import gruzomarket.ru.tools.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API для управления товарами (запчастями)")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/article/{article}")
    public ResponseEntity<ProductDTO> getProductByArticle(@PathVariable String article) {
        return ResponseEntity.ok(productService.findByArticle(article));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.findByNameContaining(name));
    }

    /**
     * Универсальный поиск/фильтрация для каталога.
     *
     * Пример:
     * /api/products/query?q=турбо&categoryIds=1,2&brandIds=3&minPrice=1000&maxPrice=50000&inStock=true&page=0&size=12&sort=price_desc
     */
    @GetMapping("/query")
    public ResponseEntity<ProductSearchResponse<ProductDTO>> queryProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoryIds,
            @RequestParam(required = false) String brandIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name_asc") String sort
    ) {
        return ResponseEntity.ok(productService.search(q, categoryIds, brandIds, minPrice, maxPrice, inStock, page, size, sort));
    }
    @GetMapping("/category-groups")
    public ResponseEntity<List<CategoryGroupDTO>> getCategoryGroups() {
        return ResponseEntity.ok(categoryService.getCategoryGroups());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.findByCategoryId(categoryId));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(productDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

