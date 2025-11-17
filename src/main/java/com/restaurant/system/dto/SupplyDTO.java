package com.restaurant.system.dto;

import com.restaurant.system.entity.enums.SupplyStatus;
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
public class SupplyDTO {
    private Long id;

    private OffsetDateTime supplyTime;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private String supplierName;

    private SupplyStatus status;

    private BigDecimal totalCost;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private Long receivedByUserId;

    private OffsetDateTime createdAt;

    @NotEmpty(message = "Supply must contain at least one item")
    @Valid
    @Builder.Default
    private List<SupplyItemDTO> items = new ArrayList<>();
}
