//package gruzomarket.ru.tools.controller;
//
//import gruzomarket.ru.tools.dto.BrandDTO;
//import gruzomarket.ru.tools.dto.CategoryDTO;
//import gruzomarket.ru.tools.service.BrandService;
//import gruzomarket.ru.tools.service.CategoryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//
//@Controller  // Важно: @Controller, а не @RestController
//@RequiredArgsConstructor
//public class CatalogController {
//    private final CategoryService categoryService;
//    private final BrandService brandService;
//
//    @GetMapping("/products")  // URL для страницы, например /catalog или /products
//    public String catalogPage(Model model) {
//        List<CategoryDTO> categories = categoryService.findAll();
//        List<BrandDTO> brands = brandService.findAll();
//        model.addAttribute("categories", categories);  // Добавляем категории
//        model.addAttribute("brands", brands);  // Добавляем бренды
//        return "products";  // Имя шаблона: products.html
//    }
//}