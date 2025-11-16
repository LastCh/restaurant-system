package com.restaurant.system.dto;

import com.restaurant.system.entity.enums.PaymentMethod;
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
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private Long orderId;
    private String receiptNumber;
    private Long processedByUserId;
}
