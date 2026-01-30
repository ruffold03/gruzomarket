package gruzomarket.ru.tools.mapper;

import gruzomarket.ru.tools.dto.OrderDTO;
import gruzomarket.ru.tools.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        return new OrderDTO(
                order.getId(),
                order.getCustomerName(),
                order.getPhone(),
                order.getEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getNotes(),
                order.getCreatedAt()
        );
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
        order.setCreatedAt(dto.getCreatedAt());
        return order;
    }
}















