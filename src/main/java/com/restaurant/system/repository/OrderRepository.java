package com.restaurant.system.repository;

import com.restaurant.system.entity.Order;
import com.restaurant.system.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByClient_Id(Long clientId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Long countByOrderTimeBetween(OffsetDateTime start, OffsetDateTime end);
    Long countByStatus(OrderStatus status);
}
