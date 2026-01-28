package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.RegisterRequest;
import gruzomarket.ru.tools.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("message", "Неверный email или пароль");
            model.addAttribute("error", true);
        }

        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }

        model.addAttribute("isRegister", false);
        model.addAttribute("title", "Вход | GruzoMarket");

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(
            @RequestParam(required = false) String success,
            Model model) {

        if (success != null) {
            model.addAttribute("message", "Регистрация прошла успешно! Теперь вы можете войти.");
        }

        model.addAttribute("isRegister", true);
        model.addAttribute("title", "Регистрация | GruzoMarket");

        return "auth/login";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute RegisterRequest request,
            Model model
    ) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Пароли не совпадают");
            return "auth";
        }

        customerService.register(request);
        return "redirect:/auth/login?registered";
    }

}
