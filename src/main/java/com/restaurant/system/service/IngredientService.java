package com.restaurant.system.service;

import com.restaurant.system.dto.IngredientDTO;
import java.util.List;
import java.util.Optional;

public interface IngredientService {
    IngredientDTO createIngredient(IngredientDTO ingredientDTO);
    Optional<IngredientDTO> getIngredientById(Long id);
    List<IngredientDTO> getAllIngredients();
    IngredientDTO updateIngredient(Long id, IngredientDTO ingredientDTO);
    void deleteIngredient(Long id);

    // Специфичные методы
    List<IngredientDTO> getLowStockIngredients();
    void updateStock(Long id, java.math.BigDecimal quantity);
}
