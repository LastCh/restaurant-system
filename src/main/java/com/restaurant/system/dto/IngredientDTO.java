package com.restaurant.system.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class IngredientDTO {
    private Long id;

    @NotBlank(message = "Ingredient name is required")
    private String name;

    @NotBlank(message = "Unit is required (kg, g, l, ml, piece)")
    private String unit;

    @DecimalMin(value = "0", message = "Stock quantity cannot be negative")
    private BigDecimal stockQuantity;

    @DecimalMin(value = "0.01", message = "Cost per unit must be greater than 0")
    private BigDecimal costPerUnit;

    @DecimalMin(value = "0", message = "Min stock level cannot be negative")
    private BigDecimal minStockLevel;

    private OffsetDateTime createdAt;
}
