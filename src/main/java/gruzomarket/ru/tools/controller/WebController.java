package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.BrandDTO;
import gruzomarket.ru.tools.dto.CategoryDTO;
import gruzomarket.ru.tools.dto.ProductDTO;
import gruzomarket.ru.tools.entity.Customer;
import gruzomarket.ru.tools.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CustomerService customerService;
    private final TelegramService telegramService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("activePage", "home");

        List<CategoryDTO> categories = categoryService.findAll();
        categories.sort((c1, c2) -> {
            int count1 = c1.getProductCount() != null ? c1.getProductCount() : 0;
            int count2 = c2.getProductCount() != null ? c2.getProductCount() : 0;
            return Integer.compare(count2, count1);
        });

        if (categories.size() > 6)
            categories = categories.subList(0, 6);
        model.addAttribute("categories", categories);

        List<ProductDTO> products = productService.findAllVisible();
        if (products.size() > 8)
            products = products.subList(0, 8);
        model.addAttribute("products", products);

        List<BrandDTO> brands = brandService.findAll();
        if (brands.size() > 6)
            brands = brands.subList(0, 6);
        model.addAttribute("brands", brands);
        return "index";
    }

    @GetMapping("/products")
    public String catalogPage(Model model) {
        List<CategoryDTO> categories = categoryService.findAll();
        List<BrandDTO> brands = brandService.findAll();
        model.addAttribute("activePage", "products");
        model.addAttribute("categories", categories); // Добавляем категории
        model.addAttribute("brands", brands); // Добавляем бренды
        return "products"; // Имя шаблона: products.html
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model, HttpSession session) {
        ProductDTO product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("activePage", "products");
        return "product_view";
    }

    @GetMapping("/brands")
    public String brands(Model model, HttpSession session) {
        model.addAttribute("activePage", "brands");
        model.addAttribute("brands", brandService.findAll());
        return "brands";
    }

    @GetMapping("/delivery")
    public String delivery(Model model, HttpSession session) {
        model.addAttribute("activePage", "delivery");
        return "delivery";
    }

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        model.addAttribute("activePage", "about");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model, HttpSession session) {
        model.addAttribute("activePage", "contact");
        return "contact";
    }

    @PostMapping("/api/contact")
    @ResponseBody
    public ResponseEntity<String> handleContactForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String message) {
        telegramService.sendContactMessage(name, email, message);
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        model.addAttribute("activePage", "cart");

        Customer current = null;
        try {
            current = customerService.getCurrentUser();
        } catch (Exception ignored) {
        }
        model.addAttribute("customer", current);
        return "cart";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
