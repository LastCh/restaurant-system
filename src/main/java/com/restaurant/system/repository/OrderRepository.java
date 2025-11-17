package com.restaurant.system.repository;

import com.restaurant.system.entity.Order;
import com.restaurant.system.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByClient_Id(Long clientId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE order_time BETWEEN :start AND :end", nativeQuery = true)
    Long countByOrderTimeBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = CAST(:status AS order_status)", nativeQuery = true)
    Long countByStatus(@Param("status") String status);
}
