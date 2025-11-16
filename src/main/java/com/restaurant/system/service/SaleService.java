package com.restaurant.system.service;

import com.restaurant.system.dto.SaleDTO;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleService {
    SaleDTO createSale(SaleDTO saleDTO);
    Optional<SaleDTO> getSaleById(Long id);
    List<SaleDTO> getAllSales();
    Optional<SaleDTO> getSaleByOrderId(Long orderId);
    List<SaleDTO> getSalesBetweenDates(OffsetDateTime start, OffsetDateTime end);
    void deleteSale(Long id);
}
