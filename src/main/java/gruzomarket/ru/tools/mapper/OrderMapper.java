package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.OrderDTO;
import gruzomarket.ru.tools.dto.OrderItemDTO;
import gruzomarket.ru.tools.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setPhone(order.getPhone());
        dto.setEmail(order.getEmail());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setNotes(order.getNotes());
        dto.setSocialLink(order.getSocialLink());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    public OrderItemDTO toItemDTO(gruzomarket.ru.tools.entity.OrderItem item) {
        if (item == null)
            return null;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrderId());
        dto.setProductId(item.getProductId());
        if (item.getProduct() != null) {
            dto.setProductName(item.getProduct().getName());
            dto.setProductArticle(item.getProduct().getArticle());
        }
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        if (dto.getUnitPrice() != null && dto.getQuantity() != null) {
            dto.setLineTotal(dto.getUnitPrice().multiply(new java.math.BigDecimal(dto.getQuantity())));
        }
        return dto;
    }

    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }
        Order order = new Order();
        order.setId(dto.getId());
        order.setCustomerName(dto.getCustomerName());
        order.setPhone(dto.getPhone());
        order.setEmail(dto.getEmail());
        order.setStatus(dto.getStatus());
        order.setTotalAmount(dto.getTotalAmount());
        order.setNotes(dto.getNotes());
        order.setSocialLink(dto.getSocialLink());
        order.setCreatedAt(dto.getCreatedAt());
        return order;
    }
}

