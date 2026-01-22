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
        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getOrderId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice()
        );
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











