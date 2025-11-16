package com.restaurant.system.repository;

import com.restaurant.system.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByCategory(String category);
    List<Dish> findByIsAvailableTrue();
    List<Dish> findByCategoryAndIsAvailableTrue(String category);
}
