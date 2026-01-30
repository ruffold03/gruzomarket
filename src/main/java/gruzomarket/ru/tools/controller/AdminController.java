package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.BrandDTO;
import gruzomarket.ru.tools.dto.CategoryDTO;
import gruzomarket.ru.tools.dto.ProductDTO;
import gruzomarket.ru.tools.dto.OrderDTO;
import gruzomarket.ru.tools.service.BrandService;
import gruzomarket.ru.tools.service.CategoryService;
import gruzomarket.ru.tools.service.OrderService;
import gruzomarket.ru.tools.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final OrderService orderService;

    private void common(Model model, HttpSession session) {
        model.addAttribute("activePage", "admin");
        // cartCount не так важен в админке, можно оставить 0
        model.addAttribute("cartCount", 0);
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("productsCount", productService.findAll().size());
        model.addAttribute("categoriesCount", categoryService.findAll().size());
        model.addAttribute("brandsCount", brandService.findAll().size());
        model.addAttribute("ordersCount", orderService.findAll().size());
        return "admin/dashboard";
    }

    // Products
    @GetMapping("/products")
    public String products(Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("products", productService.findAll());
        return "admin/products/list";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("product", new ProductDTO());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/edit";
    }

    @GetMapping("/products/{id}")
    public String editProduct(@PathVariable Long id, Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/edit";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") ProductDTO dto) {
        // Явно обнуляем ID, если он пустой или 0 (для создания нового товара)
        if (dto.getId() == null || dto.getId() <= 0) {
            dto.setId(null);
            productService.create(dto);
        } else {
            productService.update(dto.getId(), dto);
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }

    // Categories
    @GetMapping("/categories")
    public String categories(Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories/list";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("category", new CategoryDTO());
        model.addAttribute("allCategories", categoryService.findAll());
        return "admin/categories/edit";
    }

    @GetMapping("/categories/{id}")
    public String editCategory(@PathVariable Long id, Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("category", categoryService.findById(id));
        model.addAttribute("allCategories", categoryService.findAll());
        return "admin/categories/edit";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") CategoryDTO dto) {
        // Явно обнуляем ID, если он пустой или 0 (для создания новой категории)
        if (dto.getId() == null || dto.getId() <= 0) {
            dto.setId(null);
            categoryService.create(dto);
        } else {
            categoryService.update(dto.getId(), dto);
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }

    // Brands
    @GetMapping("/brands")
    public String adminBrands(Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("brands", brandService.findAll());
        return "admin/brands/list";
    }

    @GetMapping("/brands/new")
    public String newBrand(Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("brand", new BrandDTO());
        return "admin/brands/edit";
    }

    @GetMapping("/brands/{id}")
    public String editBrand(@PathVariable Long id, Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("brand", brandService.findById(id));
        return "admin/brands/edit";
    }

    @PostMapping("/brands/save")
    public String saveBrand(@ModelAttribute("brand") BrandDTO dto) {
        // Явно обнуляем ID, если он пустой или 0 (для создания нового бренда)
        if (dto.getId() == null || dto.getId() <= 0) {
            dto.setId(null);
            brandService.create(dto);
        } else {
            brandService.update(dto.getId(), dto);
        }
        return "redirect:/admin/brands";
    }

    @PostMapping("/brands/{id}/delete")
    public String deleteBrand(@PathVariable Long id) {
        brandService.delete(id);
        return "redirect:/admin/brands";
    }

    // Orders (просмотр и смена статуса)
    @GetMapping("/orders")
    public String orders(Model model, HttpSession session) {
        common(model, session);
        List<OrderDTO> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders/list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Long id, Model model, HttpSession session) {
        common(model, session);
        model.addAttribute("order", orderService.findById(id));
        return "admin/orders/view";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        OrderDTO dto = orderService.findById(id);
        dto.setStatus(status);
        orderService.update(id, dto);
        return "redirect:/admin/orders/" + id;
    }
}




