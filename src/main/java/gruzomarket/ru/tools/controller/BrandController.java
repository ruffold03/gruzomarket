package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.BrandDTO;
import gruzomarket.ru.tools.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "API для управления брендами автомобилей")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "Получить все бренды", description = "Возвращает список всех брендов автомобилей")
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        return ResponseEntity.ok(brandService.findAll());
    }

    // Новый метод для Thymeleaf (без @ResponseBody)
    @GetMapping("/page")
    public String brandsPage(Model model) {
        List<BrandDTO> brands = brandService.findAll();
        model.addAttribute("title", "Бренды");
        model.addAttribute("brands", brands);
        return "brands"; // templates/brands.html
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить бренд по ID", description = "Возвращает бренд по указанному идентификатору")
    public ResponseEntity<BrandDTO> getBrandById(
            @Parameter(description = "Идентификатор бренда") @PathVariable Long id) {
        return ResponseEntity.ok(brandService.findById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Получить бренд по имени", description = "Возвращает бренд по указанному имени")
    public ResponseEntity<BrandDTO> getBrandByName(
            @Parameter(description = "Название бренда") @PathVariable String name) {
        return ResponseEntity.ok(brandService.findByName(name));
    }

    @PostMapping
    @Operation(summary = "Создать новый бренд", description = "Создает новый бренд автомобиля")
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.create(brandDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить бренд", description = "Обновляет информацию о существующем бренде")
    public ResponseEntity<BrandDTO> updateBrand(
            @Parameter(description = "Идентификатор бренда") @PathVariable Long id,
            @RequestBody BrandDTO brandDTO) {
        return ResponseEntity.ok(brandService.update(id, brandDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить бренд", description = "Удаляет бренд по указанному идентификатору")
    public ResponseEntity<Void> deleteBrand(
            @Parameter(description = "Идентификатор бренда") @PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{name}")
    @Operation(summary = "Проверить существование бренда", description = "Проверяет, существует ли бренд с указанным именем")
    public ResponseEntity<Boolean> existsByName(
            @Parameter(description = "Название бренда") @PathVariable String name) {
        return ResponseEntity.ok(brandService.existsByName(name));
    }
}

