package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.OrderItemDTO;
import gruzomarket.ru.tools.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemDTO toDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrderId());
        dto.setProductId(orderItem.getProductId());
        if (orderItem.getProduct() != null) {
            dto.setProductName(orderItem.getProduct().getName());
            dto.setProductArticle(orderItem.getProduct().getArticle());
        }
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        if (dto.getUnitPrice() != null && dto.getQuantity() != null) {
            dto.setLineTotal(dto.getUnitPrice().multiply(new java.math.BigDecimal(dto.getQuantity())));
        }
        return dto;
    }

    public OrderItem toEntity(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setId(dto.getId());
        orderItem.setOrderId(dto.getOrderId());
        orderItem.setProductId(dto.getProductId());
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setUnitPrice(dto.getUnitPrice());
        return orderItem;
    }
}

