package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.OrderDTO;
import gruzomarket.ru.tools.dto.OrderItemDTO;
import gruzomarket.ru.tools.entity.Order;
import gruzomarket.ru.tools.repository.OrderItemRepository;
import gruzomarket.ru.tools.exception.NotFoundException;
import gruzomarket.ru.tools.mapper.OrderMapper;
import gruzomarket.ru.tools.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final TelegramService telegramService;

    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(this::populateItems)
                .collect(Collectors.toList());
    }

    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        return populateItems(order);
    }

    private OrderDTO populateItems(Order order) {
        OrderDTO dto = orderMapper.toDTO(order);
        List<OrderItemDTO> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(orderMapper::toItemDTO)
                .collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }

    public List<OrderDTO> findByStatus(String status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> findByPhone(String phone) {
        return orderRepository.findByPhone(phone).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> findByEmail(String email) {
        return orderRepository.findByEmail(email).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO create(OrderDTO dto) {
        Order order = orderMapper.toEntity(dto);
        order = orderRepository.save(order);
        OrderDTO savedDto = orderMapper.toDTO(order);

        if (telegramService != null) {
            telegramService.sendOrderNotification(populateItems(order));
        }

        return savedDto;
    }

    public OrderDTO update(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        String oldStatus = order.getStatus();

        order.setCustomerName(dto.getCustomerName());
        order.setPhone(dto.getPhone());
        order.setEmail(dto.getEmail());
        order.setStatus(dto.getStatus());
        order.setTotalAmount(dto.getTotalAmount());
        order.setNotes(dto.getNotes());
        // createdAt не обновляется

        OrderDTO updatedDto = orderMapper.toDTO(orderRepository.save(order));

        // Отправляем уведомление об изменении статуса
        if (telegramService != null && !oldStatus.equals(updatedDto.getStatus())) {
            telegramService.sendOrderStatusUpdate(
                    updatedDto.getId().toString(),
                    oldStatus,
                    updatedDto.getStatus());
        }

        return updatedDto;
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
