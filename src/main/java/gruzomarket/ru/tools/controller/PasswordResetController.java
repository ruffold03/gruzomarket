package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService resetService;

    @GetMapping("/forgot")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot")
    public String sendResetCode(@RequestParam String email, RedirectAttributes redirectAttributes) {
        resetService.generateResetCode(email);
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/auth/password-reset/verify";
    }

    @GetMapping("/verify")
    public String verifyCodeForm(@RequestParam(required = false) String email, Model model) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "auth/verify-code";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String email, @RequestParam String code,
            RedirectAttributes redirectAttributes) {
        if (resetService.verifyCode(email, code)) {
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("code", code);
            return "redirect:/auth/password-reset/reset";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Неверный или просроченный код.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/password-reset/verify";
        }
    }

    @GetMapping("/reset")
    public String resetPasswordForm(@RequestParam(required = false) String email,
            @RequestParam(required = false) String code, Model model) {
        if (email != null)
            model.addAttribute("email", email);
        if (code != null)
            model.addAttribute("code", code);
        return "auth/reset-password";
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestParam String email, @RequestParam String code,
            @RequestParam String password, @RequestParam("confirm-password") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пароли не совпадают.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("code", code);
            return "redirect:/auth/password-reset/reset";
        }
        if (resetService.resetPassword(email, code, password)) {
            redirectAttributes.addFlashAttribute("successMessage", "Пароль успешно изменен. Теперь вы можете войти.");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при смене пароля. Попробуйте снова.");
            return "redirect:/auth/password-reset/forgot";
        }
    }
}
