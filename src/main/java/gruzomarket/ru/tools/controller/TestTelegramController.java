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
        gruzomarket.ru.tools.dto.OrderDTO testOrder = new gruzomarket.ru.tools.dto.OrderDTO();
        testOrder.setId(999L);
        testOrder.setCustomerName("Тестовый Клиент");
        testOrder.setPhone("+7 (999) 000-00-00");
        testOrder.setTotalAmount(java.math.BigDecimal.valueOf(100.50));
        testOrder.setStatus("ТЕСТ");

        gruzomarket.ru.tools.dto.OrderItemDTO item1 = new gruzomarket.ru.tools.dto.OrderItemDTO();
        item1.setProductName("Тестовый Товар 1");
        item1.setQuantity(2);
        item1.setUnitPrice(java.math.BigDecimal.valueOf(25.00));

        gruzomarket.ru.tools.dto.OrderItemDTO item2 = new gruzomarket.ru.tools.dto.OrderItemDTO();
        item2.setProductName("Тестовый Товар 2");
        item2.setQuantity(1);
        item2.setUnitPrice(java.math.BigDecimal.valueOf(50.50));

        testOrder.setItems(java.util.List.of(item1, item2));

        telegramService.sendOrderNotification(testOrder);
        return "Тестовое уведомление о заказе c товарами отправлено!";
    }
}
