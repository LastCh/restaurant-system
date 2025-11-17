package com.restaurant.system.dto;

import com.restaurant.system.entity.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private Long createdByUserId;

    private OffsetDateTime createdAt;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    @Builder.Default
    private List<OrderItemDTO> items = new ArrayList<>();
}
