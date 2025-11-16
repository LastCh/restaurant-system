package com.restaurant.system.service.impl;

import com.restaurant.system.dto.IngredientDTO;
import com.restaurant.system.entity.Ingredient;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.IngredientRepository;
import com.restaurant.system.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    @Override
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        if (ingredientDTO.getName() == null || ingredientDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("The ingredient name cannot be empty");
        }
        if (ingredientDTO.getUnit() == null || ingredientDTO.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("The unit of measurement cannot be empty");
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDTO.getName());
        ingredient.setUnit(ingredientDTO.getUnit());
        ingredient.setStockQuantity(ingredientDTO.getStockQuantity() != null ?
                ingredientDTO.getStockQuantity() : BigDecimal.ZERO);
        ingredient.setCostPerUnit(ingredientDTO.getCostPerUnit() != null ?
                ingredientDTO.getCostPerUnit() : BigDecimal.ZERO);
        ingredient.setMinStockLevel(ingredientDTO.getMinStockLevel() != null ?
                ingredientDTO.getMinStockLevel() : BigDecimal.ZERO);

        return toDTO(ingredientRepository.save(ingredient));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IngredientDTO> getIngredientById(Long id) {
        return ingredientRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDTO updateIngredient(Long id, IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        if (ingredientDTO.getName() != null && !ingredientDTO.getName().isEmpty()) {
            ingredient.setName(ingredientDTO.getName());
        }
        if (ingredientDTO.getUnit() != null) {
            ingredient.setUnit(ingredientDTO.getUnit());
        }
        if (ingredientDTO.getCostPerUnit() != null) {
            ingredient.setCostPerUnit(ingredientDTO.getCostPerUnit());
        }
        if (ingredientDTO.getMinStockLevel() != null) {
            ingredient.setMinStockLevel(ingredientDTO.getMinStockLevel());
        }

        return toDTO(ingredientRepository.save(ingredient));
    }

    @Override
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new NotFoundException("Ingredient not found");
        }
        ingredientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDTO> getLowStockIngredients() {
        return ingredientRepository.findLowStockIngredients().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStock(Long id, BigDecimal quantity) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        BigDecimal newStock = ingredient.getStockQuantity().add(quantity);

        if (newStock.signum() < 0) {
            throw new IllegalArgumentException("Not enough ingredient in stock");
        }

        ingredient.setStockQuantity(newStock);
        ingredientRepository.save(ingredient);
    }

    private IngredientDTO toDTO(Ingredient ingredient) {
        return IngredientDTO.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .unit(ingredient.getUnit())
                .stockQuantity(ingredient.getStockQuantity())
                .costPerUnit(ingredient.getCostPerUnit())
                .minStockLevel(ingredient.getMinStockLevel())
                .createdAt(ingredient.getCreatedAt())
                .build();
    }
}
