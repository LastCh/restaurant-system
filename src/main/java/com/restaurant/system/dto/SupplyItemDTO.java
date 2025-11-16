package com.restaurant.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplyItemDTO {
    private Long id;
    private Long supplyId;
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
}
