package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.BrandDTO;
import gruzomarket.ru.tools.dto.CategoryDTO;
import gruzomarket.ru.tools.dto.ProductDTO;
import gruzomarket.ru.tools.service.CartService;
import gruzomarket.ru.tools.service.BrandService;
import gruzomarket.ru.tools.service.CategoryService;
import gruzomarket.ru.tools.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CartService cartService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("activePage", "home");
        model.addAttribute("cartCount", cartService.count(session));

        List<CategoryDTO> categories = categoryService.findAll();
        if (categories.size() > 6) categories = categories.subList(0, 6);
        model.addAttribute("categories", categories);

        List<ProductDTO> products = productService.findAll();
        if (products.size() > 6) products = products.subList(0, 6);
        model.addAttribute("products", products);

        List<BrandDTO> brands = brandService.findAll();
        if (brands.size() > 6) brands = brands.subList(0, 6);
        model.addAttribute("brands", brands);
        return "index";
    }

//    @GetMapping("/products")
//    public String products(Model model, HttpSession session) {
//        model.addAttribute("activePage", "products");
//        model.addAttribute("cartCount", cartService.count(session));
//        return "products";
//    }

    @GetMapping("/brands")
    public String brands(Model model, HttpSession session) {
        model.addAttribute("activePage", "brands");
        model.addAttribute("cartCount", cartService.count(session));
        model.addAttribute("brands", brandService.findAll());
        return "brands";
    }

    @GetMapping("/delivery")
    public String delivery(Model model, HttpSession session) {
        model.addAttribute("activePage", "delivery");
        model.addAttribute("cartCount", cartService.count(session));
        return "delivery";
    }

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        model.addAttribute("activePage", "about");
        model.addAttribute("cartCount", cartService.count(session));
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model, HttpSession session) {
        model.addAttribute("activePage", "contact");
        model.addAttribute("cartCount", cartService.count(session));
        return "contact";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        model.addAttribute("activePage", "cart");
        model.addAttribute("cartCount", cartService.count(session));
        return "cart";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

