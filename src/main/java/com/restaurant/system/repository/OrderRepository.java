package com.restaurant.system.repository;

import com.restaurant.system.entity.Order;
import com.restaurant.system.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClient_Id(Long clientId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByClient_IdAndStatus(Long clientId, OrderStatus status);
}
