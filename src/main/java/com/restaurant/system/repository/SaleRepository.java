package com.restaurant.system.repository;

import com.restaurant.system.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByOrderId(Long orderId);
    List<Sale> findBySaleTimeBetween(OffsetDateTime start, OffsetDateTime end);
}
