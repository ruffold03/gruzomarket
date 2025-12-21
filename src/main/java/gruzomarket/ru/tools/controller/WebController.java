package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.BrandDTO;
import gruzomarket.ru.tools.dto.CategoryDTO;
import gruzomarket.ru.tools.dto.ProductDTO;
import gruzomarket.ru.tools.service.BrandService;
import gruzomarket.ru.tools.service.CategoryService;
import gruzomarket.ru.tools.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    @GetMapping("/")
    public String index(Model model) {
        // Получаем популярные категории (первые 6)
        List<CategoryDTO> categories = categoryService.findAll();
        if (categories.size() > 6) {
            categories = categories.subList(0, 6);
        }
        model.addAttribute("categories", categories);

        // Получаем популярные товары (первые 8)
        List<ProductDTO> products = productService.findAll();
        if (products.size() > 8) {
            products = products.subList(0, 8);
        }
        model.addAttribute("products", products);

        return "index";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) List<Long> brands,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false, defaultValue = "name_asc") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        // Получаем все категории для фильтра
        List<CategoryDTO> allCategories = categoryService.findAll();
        model.addAttribute("allCategories", allCategories);
        
        // Получаем все бренды для фильтра
        List<BrandDTO> allBrands = brandService.findAll();
        model.addAttribute("allBrands", allBrands);

        // Получаем все товары
        List<ProductDTO> allProducts = productService.findAll();

        // Применяем фильтры
        List<ProductDTO> filteredProducts = allProducts.stream()
                .filter(product -> {
                    // Поиск по названию
                    if (search != null && !search.isEmpty()) {
                        if (!product.getName().toLowerCase().contains(search.toLowerCase()) &&
                            !product.getArticle().toLowerCase().contains(search.toLowerCase())) {
                            return false;
                        }
                    }

                    // Фильтр по категориям
                    if (categories != null && !categories.isEmpty()) {
                        if (product.getCategoryId() == null || !categories.contains(product.getCategoryId())) {
                            return false;
                        }
                    }

                    // Фильтр по цене
                    if (minPrice != null && product.getPrice().doubleValue() < minPrice) {
                        return false;
                    }
                    if (maxPrice != null && product.getPrice().doubleValue() > maxPrice) {
                        return false;
                    }

                    // Фильтр по наличию
                    if (inStock != null && inStock && product.getQuantity() == 0) {
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        // Сортировка
        switch (sort) {
            case "name_desc":
                filteredProducts.sort((a, b) -> b.getName().compareToIgnoreCase(a.getName()));
                break;
            case "price_asc":
                filteredProducts.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
                break;
            case "price_desc":
                filteredProducts.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                break;
            default: // name_asc
                filteredProducts.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        }

        // Пагинация
        int pageSize = 12;
        int totalPages = (int) Math.ceil((double) filteredProducts.size() / pageSize);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, filteredProducts.size());
        
        List<ProductDTO> paginatedProducts = filteredProducts.subList(start, end);

        // Создаем мапу категорий для быстрого доступа
        Map<Long, String> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(CategoryDTO::getId, CategoryDTO::getName));
        model.addAttribute("categoryMap", categoryMap);

        model.addAttribute("products", paginatedProducts);
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "products";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        List<CategoryDTO> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "categories";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

