package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestTelegramController {

    private final TelegramService telegramService;

    @GetMapping("/telegram")
    public String test() {
        telegramService.sendNotification("🔔 Тест: Бот работает!");
        return "Тест отправлен в Telegram!";
    }

    @GetMapping("/order-test")
    public String testOrderNotification() {
        telegramService.sendOrderNotification(
                "TEST-001",
                "Иван Иванов",
                "+7 (999) 123-45-67",
                java.math.BigDecimal.valueOf(15000.50),
                "НОВЫЙ"
        );
        return "Тестовое уведомление о заказе отправлено!";
    }
}
