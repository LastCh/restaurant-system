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
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long dishId;
    private String dishName;  // Для удобства — название блюда
    private Integer quantity;
    private BigDecimal unitPrice;
}
