package com.restaurant.system.service.impl;

import com.restaurant.system.dto.OrderDTO;
import com.restaurant.system.dto.OrderItemDTO;
import com.restaurant.system.entity.Client;
import com.restaurant.system.entity.Order;
import com.restaurant.system.entity.OrderItem;
import com.restaurant.system.entity.Dish;
import com.restaurant.system.entity.enums.OrderStatus;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.ClientRepository;
import com.restaurant.system.repository.OrderRepository;
import com.restaurant.system.repository.OrderItemRepository;
import com.restaurant.system.repository.DishRepository;
import com.restaurant.system.service.OrderService;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new NotFoundException("Клиент не найден"));

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
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClient_Id(clientId)  // ← измени
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));
        order.setStatus(status);
        return toDTO(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Заказ не найден");
        }
        orderRepository.deleteById(id);
    }

    @Override
    public OrderItemDTO addItemToOrder(Long orderId, OrderItemDTO itemDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));

        Dish dish = dishRepository.findById(itemDTO.getDishId())
                .orElseThrow(() -> new NotFoundException("Блюдо не найдено"));

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
                .orElseThrow(() -> new NotFoundException("Позиция не найдена"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Позиция не принадлежит этому заказу");
        }

        orderItemRepository.deleteById(itemId);
    }

    @Override
    public OrderDTO completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));

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
