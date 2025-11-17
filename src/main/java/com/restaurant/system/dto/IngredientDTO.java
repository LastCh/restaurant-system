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
public class IngredientDTO {
    private Long id;

    @NotBlank(message = "Ingredient name is required")
    @Size(min = 2, max = 255, message = "Ingredient name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Unit is required (kg, g, l, ml, piece)")
    @Pattern(
            regexp = "^(kg|g|l|ml|piece|pcs|unit)$",
            message = "Unit must be one of: kg, g, l, ml, piece, pcs, unit"
    )
    private String unit;

    @NotNull(message = "Stock quantity is required")
    @DecimalMin(value = "0", message = "Stock quantity cannot be negative")
    @DecimalMax(value = "999999.99", message = "Stock quantity cannot exceed 999999.99")
    private BigDecimal stockQuantity;

    @NotNull(message = "Cost per unit is required")
    @DecimalMin(value = "0.01", message = "Cost per unit must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Cost per unit cannot exceed 999999.99")
    private BigDecimal costPerUnit;

    @NotNull(message = "Minimum stock level is required")
    @DecimalMin(value = "0", message = "Min stock level cannot be negative")
    @DecimalMax(value = "999999.99", message = "Min stock level cannot exceed 999999.99")
    private BigDecimal minStockLevel;

    private OffsetDateTime createdAt;
}
