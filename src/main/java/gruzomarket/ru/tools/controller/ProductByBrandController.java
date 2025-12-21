package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.ProductByBrandDTO;
import gruzomarket.ru.tools.service.ProductByBrandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-brands")
@RequiredArgsConstructor
@Tag(name = "Product-Brand Relations", description = "API для управления связями товаров и брендов")
public class ProductByBrandController {

    private final ProductByBrandService productByBrandService;

    @GetMapping
    public ResponseEntity<List<ProductByBrandDTO>> getAllProductByBrands() {
        return ResponseEntity.ok(productByBrandService.findAll());
    }

    @GetMapping("/product/{productId}/brand/{brandId}")
    public ResponseEntity<ProductByBrandDTO> getProductByBrand(
            @PathVariable Long productId, 
            @PathVariable Long brandId) {
        return ResponseEntity.ok(productByBrandService.findById(productId, brandId));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductByBrandDTO>> getByBrandId(@PathVariable Long brandId) {
        return ResponseEntity.ok(productByBrandService.findByBrandId(brandId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductByBrandDTO>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productByBrandService.findByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<ProductByBrandDTO> createProductByBrand(@RequestBody ProductByBrandDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productByBrandService.create(dto));
    }

    @DeleteMapping("/product/{productId}/brand/{brandId}")
    public ResponseEntity<Void> deleteProductByBrand(
            @PathVariable Long productId, 
            @PathVariable Long brandId) {
        productByBrandService.delete(productId, brandId);
        return ResponseEntity.noContent().build();
    }
}

