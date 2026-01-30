package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.ProfileUpdateRequest;
import gruzomarket.ru.tools.service.CartService;
import gruzomarket.ru.tools.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CustomerService customerService;
    private final CartService cartService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model, HttpSession session) {
        model.addAttribute("activePage", "profile");
        model.addAttribute("cartCount", cartService.count(session));
        model.addAttribute("user", customerService.getCurrentUser());
        model.addAttribute("updateRequest", new ProfileUpdateRequest());
        model.addAttribute("title", "Личный кабинет | GruzoMarket");
        return "profile/index";
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(
            @ModelAttribute("updateRequest") ProfileUpdateRequest request,
            RedirectAttributes redirectAttributes) {
        
        try {
            customerService.updateProfile(request);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении профиля: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
}
