package com.restaurant.system.repository;

import com.restaurant.system.entity.Supply;
import com.restaurant.system.entity.enums.SupplyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {
    Page<Supply> findBySupplier_Id(Long supplierId, Pageable pageable);
    Page<Supply> findByStatus(SupplyStatus status, Pageable pageable);
}