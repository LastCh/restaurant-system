package com.restaurant.system.dto;

import jakarta.validation.constraints.*;
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
    @Size(min = 2, max = 255, message = "Dish name must be between 2 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 100, message = "Category must be between 2 and 100 characters")
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private BigDecimal price;

    private Boolean isAvailable;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Pattern(
            regexp = "^(https?://.*\\.(jpg|jpeg|png|gif|webp))?$",
            message = "Image URL must be a valid HTTP(S) URL ending with .jpg, .jpeg, .png, .gif, or .webp"
    )
    private String imageUrl;

    @NotNull(message = "Preparation time is required")
    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @Max(value = 480, message = "Preparation time cannot exceed 480 minutes (8 hours)")
    private Integer preparationTimeMinutes;

    private OffsetDateTime createdAt;
}
