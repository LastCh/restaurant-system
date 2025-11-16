package com.restaurant.system.dto;

import com.restaurant.system.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;

    private BigDecimal total;

    private OrderStatus status;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    private Long reservationId;

    private String notes;

    private Long createdByUserId;

    private OffsetDateTime createdAt;

    @Builder.Default
    private List<OrderItemDTO> items = new ArrayList<>();
}
