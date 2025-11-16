package com.restaurant.system.service;

import com.restaurant.system.dto.SaleDTO;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface SaleService {
    SaleDTO createSale(SaleDTO saleDTO);

    Optional<SaleDTO> getSaleById(Long id);

    Page<SaleDTO> getAllSales(int page, int size, String sortBy, String direction);

    Optional<SaleDTO> getSaleByOrderId(Long orderId);

    Page<SaleDTO> getSalesBetweenDates(OffsetDateTime start, OffsetDateTime end, int page, int size, String sortBy, String direction);

    void deleteSale(Long id);
}
