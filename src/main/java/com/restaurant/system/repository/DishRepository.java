package com.restaurant.system.repository;

import com.restaurant.system.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    Page<Dish> findByCategory(String category, Pageable pageable);
    Page<Dish> findByIsAvailableTrue(Pageable pageable);
    Page<Dish> findByCategoryAndIsAvailableTrue(String category, Pageable pageable);
}
