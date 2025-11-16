package com.restaurant.system.controller;

import com.restaurant.system.dto.DishDTO;
import com.restaurant.system.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @PostMapping
    public ResponseEntity<DishDTO> createDish(@Valid @RequestBody DishDTO dishDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dishService.createDish(dishDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDTO> getDishById(@PathVariable Long id) {
        return dishService.getDishById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DishDTO>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<DishDTO>> getDishesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(dishService.getDishesByCategory(category));
    }

    @GetMapping("/available")
    public ResponseEntity<List<DishDTO>> getAvailableDishes() {
        return ResponseEntity.ok(dishService.getAvailableDishes());
    }

    @GetMapping("/available/{category}")
    public ResponseEntity<List<DishDTO>> getAvailableDishesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(dishService.getAvailableDishesByCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishDTO> updateDish(
            @PathVariable Long id,
            @Valid @RequestBody DishDTO dishDTO) {
        return ResponseEntity.ok(dishService.updateDish(id, dishDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}
