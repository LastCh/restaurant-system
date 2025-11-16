package com.restaurant.system.service;

import com.restaurant.system.dto.DishDTO;
import java.util.List;
import java.util.Optional;

public interface DishService {
    DishDTO createDish(DishDTO dishDTO);
    Optional<DishDTO> getDishById(Long id);
    List<DishDTO> getAllDishes();
    List<DishDTO> getDishesByCategory(String category);
    List<DishDTO> getAvailableDishes();
    List<DishDTO> getAvailableDishesByCategory(String category);
    DishDTO updateDish(Long id, DishDTO dishDTO);
    void deleteDish(Long id);
}
