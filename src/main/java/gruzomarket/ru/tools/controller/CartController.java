package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.*;
import gruzomarket.ru.tools.entity.Order;
import gruzomarket.ru.tools.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartSummaryDTO> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.summary(session));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> count(HttpSession session) {
        return ResponseEntity.ok(cartService.count(session));
    }

    @PostMapping("/add")
    public ResponseEntity<CartSummaryDTO> add(@RequestBody CartUpdateRequest req, HttpSession session) {
        cartService.add(session, req.getProductId(), req.getQuantity() == null ? 1 : req.getQuantity());
        return ResponseEntity.ok(cartService.summary(session));
    }

    @PostMapping("/update")
    public ResponseEntity<CartSummaryDTO> update(@RequestBody CartUpdateRequest req, HttpSession session) {
        cartService.update(session, req.getProductId(), req.getQuantity() == null ? 0 : req.getQuantity());
        return ResponseEntity.ok(cartService.summary(session));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartSummaryDTO> remove(@PathVariable Long productId, HttpSession session) {
        cartService.remove(session, productId);
        return ResponseEntity.ok(cartService.summary(session));
    }

    @PostMapping("/clear")
    public ResponseEntity<CartSummaryDTO> clear(HttpSession session) {
        cartService.clear(session);
        return ResponseEntity.ok(cartService.summary(session));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(@RequestBody CheckoutRequest req, HttpSession session) {
        Order order = cartService.checkout(session, req.getCustomerName(), req.getPhone(), req.getEmail(), req.getNotes());
        OrderDTO dto = new OrderDTO(
                order.getId(),
                order.getCustomerName(),
                order.getPhone(),
                order.getEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getNotes(),
                order.getCreatedAt()
        );
        return ResponseEntity.ok(dto);
    }
}







