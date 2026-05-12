package gruzomarket.ru.tools.advice;

import gruzomarket.ru.tools.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CartService cartService;

    @ModelAttribute("cartCount")
    public int getCartCount(HttpSession session) {
        return cartService.count(session);
    }
}
