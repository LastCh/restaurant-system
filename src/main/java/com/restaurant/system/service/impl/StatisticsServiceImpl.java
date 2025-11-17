package com.restaurant.system.service.impl;

import com.restaurant.system.dto.statistics.DashboardStatsDTO;
import com.restaurant.system.entity.enums.OrderStatus;
import com.restaurant.system.entity.enums.ReservationStatus;
import com.restaurant.system.repository.*;
import com.restaurant.system.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final SaleRepository saleRepository;
    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        OffsetDateTime startOfDay = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endOfDay = startOfDay.plusDays(1);

        Long todayOrders = orderRepository.countByOrderTimeBetween(startOfDay, endOfDay);
        if (todayOrders == null) todayOrders = 0L;

        BigDecimal todayRevenue = saleRepository.sumTotalBySaleTimeBetween(startOfDay, endOfDay);
        if (todayRevenue == null) {
            todayRevenue = BigDecimal.ZERO;
        }

        Long activeReservations = reservationRepository.countByStatus(ReservationStatus.ACTIVE);
        if (activeReservations == null) activeReservations = 0L;

        Long totalClients = clientRepository.count();

        Long lowStockItems = ingredientRepository.countLowStockItems();
        if (lowStockItems == null) lowStockItems = 0L;

        Long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING.name());
        if (pendingOrders == null) pendingOrders = 0L;

        return DashboardStatsDTO.builder()
                .todayOrders(todayOrders)
                .todayRevenue(todayRevenue)
                .activeReservations(activeReservations)
                .totalClients(totalClients)
                .lowStockItems(lowStockItems)
                .pendingOrders(pendingOrders)
                .build();
    }


    @Override
    public Map<String, Object> getSalesStats(LocalDate from, LocalDate to) {
        OffsetDateTime startDate = from.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDate = to.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        BigDecimal totalRevenue = saleRepository.sumTotalBySaleTimeBetween(startDate, endDate);
        Long totalOrders = orderRepository.countByOrderTimeBetween(startDate, endDate);

        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        if (totalOrders == null) totalOrders = 0L;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalOrders", totalOrders);
        stats.put("averageOrderValue", totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO);

        return stats;
    }
}
