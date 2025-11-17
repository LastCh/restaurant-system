package com.restaurant.system.service;

import com.restaurant.system.dto.OrderDTO;
import com.restaurant.system.dto.OrderItemDTO;
import com.restaurant.system.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);

    Optional<OrderDTO> getOrderById(Long id);

    Page<OrderDTO> getAllOrders(int page, int size, String sortBy, String direction);

    Page<OrderDTO> getOrdersByClientId(Long clientId, int page, int size, String sortBy, String direction);

    Page<OrderDTO> getOrdersByStatus(OrderStatus status, int page, int size, String sortBy, String direction);

    OrderDTO updateOrderStatus(Long id, OrderStatus status);

    void deleteOrder(Long id);

    OrderItemDTO addItemToOrder(Long orderId, OrderItemDTO itemDTO);

    List<OrderItemDTO> getOrderItems(Long orderId);

    void removeItemFromOrder(Long orderId, Long itemId);

    OrderDTO completeOrder(Long id);
}
