package com.restaurant.system.service.impl;

import com.restaurant.system.dto.OrderDTO;
import com.restaurant.system.dto.OrderItemDTO;
import com.restaurant.system.entity.Client;
import com.restaurant.system.entity.Order;
import com.restaurant.system.entity.OrderItem;
import com.restaurant.system.entity.Dish;
import com.restaurant.system.entity.enums.OrderStatus;
import com.restaurant.system.exception.BadRequestException;
import com.restaurant.system.exception.ConflictException;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.ClientRepository;
import com.restaurant.system.repository.OrderRepository;
import com.restaurant.system.repository.OrderItemRepository;
import com.restaurant.system.repository.DishRepository;
import com.restaurant.system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Client client = clientRepository.findById(orderDTO.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found" ));

        Order order = new Order();
        order.setClient(client);  // ← Устанавливаем объект, не ID
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.ZERO);
        order.setNotes(orderDTO.getNotes());

        return toDTO(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return orderRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByClientId(Long clientId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return orderRepository.findByClient_Id(clientId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return orderRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Business logic: cannot change completed order status
        if (order.getStatus() == OrderStatus.COMPLETED && status != OrderStatus.COMPLETED) {
            throw new ConflictException("Cannot change status of completed order");
        }

        // Business logic: cannot mark empty order as completed
        if (status == OrderStatus.COMPLETED && order.getOrderItems().isEmpty()) {
            throw new BadRequestException("Cannot complete order without items");
        }

        order.setStatus(status);
        return toDTO(orderRepository.save(order));
    }


    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    @Override
    public OrderItemDTO addItemToOrder(Long orderId, OrderItemDTO itemDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Dish dish = dishRepository.findById(itemDTO.getDishId())
                .orElseThrow(() -> new NotFoundException("Dish not found"));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setDish(dish);
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(dish.getPrice());

        OrderItem saved = orderItemRepository.save(item);

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void removeItemFromOrder(Long orderId, Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Order item not found"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new BadRequestException("Item does not belong to this order");
        }

        orderItemRepository.deleteById(itemId);
    }


    @Override
    public OrderDTO completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        order.setStatus(OrderStatus.COMPLETED);

        return toDTO(orderRepository.save(order));
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> items = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .total(order.getTotal())
                .status(order.getStatus())
                .clientId(order.getClient() != null ? order.getClient().getId() : null)
                .reservationId(order.getReservation() != null ? order.getReservation().getId() : null)
                .notes(order.getNotes())
                .createdByUserId(order.getCreatedBy() != null ? order.getCreatedBy().getId() : null)
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemDTO toDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .dishId(item.getDish().getId())
                .dishName(item.getDish().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }

}
