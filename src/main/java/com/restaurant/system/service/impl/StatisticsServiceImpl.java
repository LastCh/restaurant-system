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

        // Count today's orders
        Long todayOrders = orderRepository.countByOrderTimeBetween(startOfDay, endOfDay);

        // Calculate today's revenue
        BigDecimal todayRevenue = saleRepository.sumTotalBySaleTimeBetween(startOfDay, endOfDay);
        if (todayRevenue == null) {
            todayRevenue = BigDecimal.ZERO;
        }

        // Count active reservations
        Long activeReservations = reservationRepository.countByStatus(ReservationStatus.ACTIVE);

        // Count total clients
        Long totalClients = clientRepository.count();

        // Count low stock items
        Long lowStockItems = ingredientRepository.countLowStockItems();

        // Count pending orders
        Long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);

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

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        stats.put("totalOrders", totalOrders);
        stats.put("averageOrderValue", totalOrders > 0 && totalRevenue != null
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO);

        return stats;
    }
}
