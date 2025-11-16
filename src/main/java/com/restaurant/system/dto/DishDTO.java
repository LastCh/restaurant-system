package com.restaurant.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishDTO {
    private Long id;

    @NotBlank(message = "Dish name is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private Boolean isAvailable;

    private String imageUrl;

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    private Integer preparationTimeMinutes;

    private OffsetDateTime createdAt;
}
