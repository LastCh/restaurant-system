package com.restaurant.system.repository;

import com.restaurant.system.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByOrder_Id(Long orderId);
    Page<Sale> findBySaleTimeBetween(OffsetDateTime start, OffsetDateTime end, Pageable pageable);

    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.saleTime BETWEEN :start AND :end")
    BigDecimal sumTotalBySaleTimeBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
