package com.restaurant.system.controller;

import com.restaurant.system.dto.IngredientDTO;
import com.restaurant.system.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<IngredientDTO> createIngredient(@Valid  @RequestBody IngredientDTO ingredientDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredientService.createIngredient(ingredientDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<IngredientDTO>> getLowStockIngredients() {
        return ResponseEntity.ok(ingredientService.getLowStockIngredients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientDTO ingredientDTO) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, ingredientDTO));
    }

    @PostMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam BigDecimal quantity) {
        ingredientService.updateStock(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
