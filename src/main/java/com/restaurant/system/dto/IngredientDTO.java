package com.restaurant.system.dto;

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
    private String name;
    private String unit;  // kg, g, l, ml, piece
    private BigDecimal stockQuantity;
    private BigDecimal costPerUnit;
    private BigDecimal minStockLevel;
    private OffsetDateTime createdAt;
}
