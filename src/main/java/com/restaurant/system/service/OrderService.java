package com.restaurant.system.service;

import com.restaurant.system.dto.OrderDTO;
import com.restaurant.system.dto.OrderItemDTO;
import com.restaurant.system.entity.enums.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    Optional<OrderDTO> getOrderById(Long id);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByClientId(Long clientId);
    List<OrderDTO> getOrdersByStatus(OrderStatus status);
    OrderDTO updateOrderStatus(Long id, OrderStatus status);
    void deleteOrder(Long id);

    // Работа с позициями заказа
    OrderItemDTO addItemToOrder(Long orderId, OrderItemDTO itemDTO);
    List<OrderItemDTO> getOrderItems(Long orderId);
    void removeItemFromOrder(Long orderId, Long itemId);
    OrderDTO completeOrder(Long id);
}
