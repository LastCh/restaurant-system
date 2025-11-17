package com.restaurant.system.service;

import com.restaurant.system.dto.statistics.DashboardStatsDTO;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {
    DashboardStatsDTO getDashboardStats();
    Map<String, Object> getSalesStats(LocalDate from, LocalDate to);
}
