package gruzomarket.ru.tools.controller;

import lombok.extern.slf4j.Slf4j;
import gruzomarket.ru.tools.dto.OrderDTO;
import gruzomarket.ru.tools.service.OrderService;
import gruzomarket.ru.tools.service.TelegramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API для управления заказами")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final TelegramService telegramService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.findByStatus(status));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<List<OrderDTO>> getOrdersByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(orderService.findByPhone(phone));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<OrderDTO>> getOrdersByEmail(@PathVariable String email) {
        return ResponseEntity.ok(orderService.findByEmail(email));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        log.info("Создание заказа через API для клиента: {}", orderDTO.getCustomerName());

        OrderDTO createdOrder = orderService.create(orderDTO);

        // Отправляем уведомление в Telegram
        try {
            telegramService.sendOrderNotification(createdOrder);
            log.info("Telegram уведомление отправлено для заказа #{}", createdOrder.getId());
        } catch (Exception e) {
            log.error("Не удалось отправить Telegram уведомление: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        log.info("Обновление заказа #{}", id);

        // Получаем текущий заказ для сравнения статусов
        OrderDTO oldOrder = orderService.findById(id);

        OrderDTO updatedOrder = orderService.update(id, orderDTO);

        // Отправляем уведомление об изменении статуса
        if (!oldOrder.getStatus().equals(updatedOrder.getStatus())) {
            try {
                telegramService.sendOrderStatusUpdate(
                        updatedOrder.getId().toString(),
                        oldOrder.getStatus(),
                        updatedOrder.getStatus());
                log.info("Telegram уведомление об изменении статуса отправлено");
            } catch (Exception e) {
                log.error("Не удалось отправить Telegram уведомление: {}", e.getMessage());
            }
        }

        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
