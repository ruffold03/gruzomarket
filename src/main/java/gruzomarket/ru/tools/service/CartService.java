package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.CartItemDTO;
import gruzomarket.ru.tools.dto.CartSummaryDTO;
import gruzomarket.ru.tools.entity.Order;
import gruzomarket.ru.tools.entity.OrderItem;
import gruzomarket.ru.tools.entity.Product;
import gruzomarket.ru.tools.exception.NotFoundException;
import gruzomarket.ru.tools.repository.OrderItemRepository;
import gruzomarket.ru.tools.repository.OrderRepository;
import gruzomarket.ru.tools.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private static final String CART_KEY = "CART";

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getOrCreateCart(HttpSession session) {
        Object existing = session.getAttribute(CART_KEY);
        if (existing instanceof Map) {
            return (Map<Long, Integer>) existing;
        }
        Map<Long, Integer> cart = new LinkedHashMap<>();
        session.setAttribute(CART_KEY, cart);
        return cart;
    }

    public int count(HttpSession session) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void add(HttpSession session, Long productId, int qty) {
        if (qty <= 0) qty = 1;
        Map<Long, Integer> cart = getOrCreateCart(session);
        cart.merge(productId, qty, Integer::sum);
    }

    public void update(HttpSession session, Long productId, int qty) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        if (qty <= 0) {
            cart.remove(productId);
        } else {
            cart.put(productId, qty);
        }
    }

    public void remove(HttpSession session, Long productId) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        cart.remove(productId);
    }

    public void clear(HttpSession session) {
        getOrCreateCart(session).clear();
    }

    @Transactional(readOnly = true)
    public CartSummaryDTO summary(HttpSession session) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        List<CartItemDTO> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int totalItems = 0;

        for (Map.Entry<Long, Integer> e : cart.entrySet()) {
            Long productId = e.getKey();
            int qty = e.getValue() == null ? 0 : e.getValue();
            if (qty <= 0) continue;

            Product p = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

            BigDecimal unit = p.getPrice() == null ? BigDecimal.ZERO : p.getPrice();
            BigDecimal line = unit.multiply(BigDecimal.valueOf(qty));

            total = total.add(line);
            totalItems += qty;

            Long categoryId = p.getCategory() != null ? p.getCategory().getId() : null;
            items.add(new CartItemDTO(
                    p.getId(),
                    p.getName(),
                    p.getArticle(),
                    unit,
                    p.getQuantity(),
                    qty,
                    line,
                    categoryId
            ));
        }

        return new CartSummaryDTO(items, totalItems, total);
    }

    public Order checkout(HttpSession session, String customerName, String phone, String email, String notes) {
        CartSummaryDTO summary = summary(session);
        if (summary.getItems() == null || summary.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = "Клиент";
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        Order order = new Order();
        order.setCustomerName(customerName.trim());
        order.setPhone(phone.trim());
        order.setEmail(email.trim());
        order.setStatus("НОВЫЙ");
        order.setTotalAmount(summary.getTotalAmount());
        order.setNotes(notes);
        order = orderRepository.save(order);

        for (CartItemDTO item : summary.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(productRepository.getReferenceById(item.getProductId()));
            oi.setQuantity(item.getQuantity());
            oi.setUnitPrice(item.getUnitPrice());
            orderItemRepository.save(oi);
        }

        clear(session);
        return order;
    }
}







