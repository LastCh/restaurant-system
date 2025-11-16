package com.restaurant.system.repository;

import com.restaurant.system.entity.Supply;
import com.restaurant.system.entity.enums.SupplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findBySupplier_Id(Long supplierId);
    List<Supply> findByStatus(SupplyStatus status);
}
