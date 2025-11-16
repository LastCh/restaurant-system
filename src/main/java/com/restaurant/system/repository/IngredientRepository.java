package com.restaurant.system.repository;

import com.restaurant.system.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("SELECT i FROM Ingredient i WHERE i.stockQuantity <= i.minStockLevel")
    List<Ingredient> findLowStockIngredients();

    List<Ingredient> findByStockQuantityLessThanEqual(BigDecimal quantity);
}
