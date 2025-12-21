package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.OrderItemDTO;
import gruzomarket.ru.tools.entity.OrderItem;
import gruzomarket.ru.tools.exception.NotFoundException;
import gruzomarket.ru.tools.mapper.OrderItemMapper;
import gruzomarket.ru.tools.repository.OrderItemRepository;
import gruzomarket.ru.tools.repository.OrderRepository;
import gruzomarket.ru.tools.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    public List<OrderItemDTO> findAll() {
        return orderItemRepository.findAll().stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderItemDTO findById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("OrderItem not found with id: " + id));
        return orderItemMapper.toDTO(orderItem);
    }

    public List<OrderItemDTO> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderItemDTO> findByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId).stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderItemDTO create(OrderItemDTO dto) {
        if (!orderRepository.existsById(dto.getOrderId())) {
            throw new NotFoundException("Order not found with id: " + dto.getOrderId());
        }
        
        if (!productRepository.existsById(dto.getProductId())) {
            throw new NotFoundException("Product not found with id: " + dto.getProductId());
        }
        
        OrderItem orderItem = orderItemMapper.toEntity(dto);
        orderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toDTO(orderItem);
    }

    public OrderItemDTO update(Long id, OrderItemDTO dto) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("OrderItem not found with id: " + id));
        
        if (!orderRepository.existsById(dto.getOrderId())) {
            throw new NotFoundException("Order not found with id: " + dto.getOrderId());
        }
        
        if (!productRepository.existsById(dto.getProductId())) {
            throw new NotFoundException("Product not found with id: " + dto.getProductId());
        }
        
        orderItem.setOrderId(dto.getOrderId());
        orderItem.setProductId(dto.getProductId());
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setUnitPrice(dto.getUnitPrice());
        
        orderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toDTO(orderItem);
    }

    public void delete(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new NotFoundException("OrderItem not found with id: " + id);
        }
        orderItemRepository.deleteById(id);
    }
}

