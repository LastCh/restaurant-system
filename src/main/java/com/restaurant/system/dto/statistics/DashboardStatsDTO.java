package com.restaurant.system.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long todayOrders;
    private BigDecimal todayRevenue;
    private Long activeReservations;
    private Long totalClients;
    private Long lowStockItems;
    private Long pendingOrders;
}
