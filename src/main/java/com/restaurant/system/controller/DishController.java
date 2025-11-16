package com.restaurant.system.controller;

import com.restaurant.system.dto.DishDTO;
import com.restaurant.system.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Dish management endpoints")
public class DishController {

    private final DishService dishService;

    @PostMapping
    @Operation(summary = "Create a new dish")
    public ResponseEntity<DishDTO> createDish(@Valid @RequestBody DishDTO dishDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dishService.createDish(dishDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dish by ID")
    public ResponseEntity<DishDTO> getDishById(@PathVariable Long id) {
        return dishService.getDishById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all dishes with pagination")
    public ResponseEntity<Page<DishDTO>> getAllDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(dishService.getAllDishes(page, size, sortBy, direction));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get dishes by category")
    public ResponseEntity<Page<DishDTO>> getDishesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(dishService.getDishesByCategory(category, page, size, sortBy, direction));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available dishes")
    public ResponseEntity<Page<DishDTO>> getAvailableDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(dishService.getAvailableDishes(page, size, sortBy, direction));
    }

    @GetMapping("/available/category/{category}")
    @Operation(summary = "Get available dishes by category")
    public ResponseEntity<Page<DishDTO>> getAvailableDishesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(dishService.getAvailableDishesByCategory(category, page, size, sortBy, direction));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update dish")
    public ResponseEntity<DishDTO> updateDish(
            @PathVariable Long id,
            @Valid @RequestBody DishDTO dishDTO) {
        return ResponseEntity.ok(dishService.updateDish(id, dishDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dish")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}