package com.restaurant.system.service;

import com.restaurant.system.dto.DishDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface DishService {
    DishDTO createDish(DishDTO dishDTO);

    Optional<DishDTO> getDishById(Long id);

    Page<DishDTO> getAllDishes(int page, int size, String sortBy, String direction);

    Page<DishDTO> getDishesByCategory(String category, int page, int size, String sortBy, String direction);

    Page<DishDTO> getAvailableDishes(int page, int size, String sortBy, String direction);

    Page<DishDTO> getAvailableDishesByCategory(String category, int page, int size, String sortBy, String direction);

    DishDTO updateDish(Long id, DishDTO dishDTO);

    void deleteDish(Long id);
}
