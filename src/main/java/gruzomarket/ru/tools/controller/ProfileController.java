package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.ProfileUpdateRequest;
import gruzomarket.ru.tools.service.CartService;
import gruzomarket.ru.tools.service.CustomerService;
import gruzomarket.ru.tools.service.FavoriteService;
import gruzomarket.ru.tools.mapper.ProductMapper;
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
    private final FavoriteService favoriteService;
    private final ProductMapper productMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model, HttpSession session) {
        model.addAttribute("activePage", "profile");
        model.addAttribute("cartCount", cartService.count(session));

        // Получаем текущего пользователя
        var customer = customerService.getCurrentUser();
        model.addAttribute("user", customer);

        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setFirstName(customer.getFirstName());
        updateRequest.setLastName(customer.getLastName());
        updateRequest.setCity(customer.getCity());
        updateRequest.setEmail(customer.getEmail());
        updateRequest.setPhone(customer.getPhone());
        updateRequest.setSocialLink(customer.getSocialLink());

        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("title", "Личный кабинет | GruzoMarket");

        // Получаем избранные товары и маппим их в DTO для безопасности рендеринга
        var favoriteProducts = favoriteService.getFavoriteProducts(customer.getEmail());
        var favoriteDTOs = favoriteProducts.stream()
                .map(productMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("favoriteProducts", favoriteDTOs);
        model.addAttribute("favoriteCount", favoriteDTOs.size());

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

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteProfile(RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCurrentProfile();
            redirectAttributes.addFlashAttribute("successMessage", "Ваш профиль был успешно деактивирован.");
            return "redirect:/auth/login?logout=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении профиля: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}
