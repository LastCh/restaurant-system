package com.restaurant.system.service;

import com.restaurant.system.dto.IngredientDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IngredientService {
    IngredientDTO createIngredient(IngredientDTO ingredientDTO);

    Optional<IngredientDTO> getIngredientById(Long id);

    Page<IngredientDTO> getAllIngredients(int page, int size, String sortBy, String direction);

    IngredientDTO updateIngredient(Long id, IngredientDTO ingredientDTO);

    void deleteIngredient(Long id);

    List<IngredientDTO> getLowStockIngredients();

    void updateStock(Long id, BigDecimal quantity);
}
