package com.restaurant.system.repository;

import com.restaurant.system.entity.SupplyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyItemRepository extends JpaRepository<SupplyItem, Long> {
    List<SupplyItem> findBySupplyId(Long supplyId);
}
