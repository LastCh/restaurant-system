package com.restaurant.system.service.impl;

import com.restaurant.system.dto.DishDTO;
import com.restaurant.system.entity.Dish;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.DishRepository;
import com.restaurant.system.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    @Override
    public DishDTO createDish(DishDTO dishDTO) {
        if (dishDTO.getName() == null || dishDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("The dish name cannot be empty");
        }
        if (dishDTO.getPrice() == null || dishDTO.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("The price must be greater than zero");
        }

        Dish dish = new Dish();
        dish.setName(dishDTO.getName());
        dish.setDescription(dishDTO.getDescription());
        dish.setCategory(dishDTO.getCategory());
        dish.setPrice(dishDTO.getPrice());
        dish.setIsAvailable(dishDTO.getIsAvailable() != null ? dishDTO.getIsAvailable() : true);
        dish.setImageUrl(dishDTO.getImageUrl());
        dish.setPreparationTimeMinutes(dishDTO.getPreparationTimeMinutes());

        return toDTO(dishRepository.save(dish));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DishDTO> getDishById(Long id) {
        return dishRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DishDTO> getAllDishes(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return dishRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DishDTO> getDishesByCategory(String category, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return dishRepository.findByCategory(category, pageable).map(this::toDTO);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<DishDTO> getAvailableDishes(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return dishRepository.findByIsAvailableTrue(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DishDTO> getAvailableDishesByCategory(String category, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return dishRepository.findByCategoryAndIsAvailableTrue(category, pageable).map(this::toDTO);
    }

    @Override
    public DishDTO updateDish(Long id, DishDTO dishDTO) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found"));

        if (dishDTO.getName() != null && !dishDTO.getName().isEmpty()) {
            dish.setName(dishDTO.getName());
        }
        if (dishDTO.getDescription() != null) {
            dish.setDescription(dishDTO.getDescription());
        }
        if (dishDTO.getCategory() != null) {
            dish.setCategory(dishDTO.getCategory());
        }
        if (dishDTO.getPrice() != null) {
            dish.setPrice(dishDTO.getPrice());
        }
        if (dishDTO.getIsAvailable() != null) {
            dish.setIsAvailable(dishDTO.getIsAvailable());
        }
        if (dishDTO.getImageUrl() != null) {
            dish.setImageUrl(dishDTO.getImageUrl());
        }
        if (dishDTO.getPreparationTimeMinutes() != null) {
            dish.setPreparationTimeMinutes(dishDTO.getPreparationTimeMinutes());
        }

        return toDTO(dishRepository.save(dish));
    }

    @Override
    public void deleteDish(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new NotFoundException("Dish not found");
        }
        dishRepository.deleteById(id);
    }

    private DishDTO toDTO(Dish dish) {
        return DishDTO.builder()
                .id(dish.getId())
                .name(dish.getName())
                .description(dish.getDescription())
                .category(dish.getCategory())
                .price(dish.getPrice())
                .isAvailable(dish.getIsAvailable())
                .imageUrl(dish.getImageUrl())
                .preparationTimeMinutes(dish.getPreparationTimeMinutes())
                .createdAt(dish.getCreatedAt())
                .build();
    }
}
