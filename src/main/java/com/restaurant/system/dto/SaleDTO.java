package com.restaurant.system.dto;

import com.restaurant.system.entity.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class SaleDTO {
    private Long id;

    private OffsetDateTime saleTime;

    @DecimalMin(value = "0.01", message = "Total must be greater than 0")
    private BigDecimal total;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Order ID is required")
    private Long orderId;

    private String receiptNumber;

    private Long processedByUserId;
}
