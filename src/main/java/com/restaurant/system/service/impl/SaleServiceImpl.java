package com.restaurant.system.service.impl;

import com.restaurant.system.dto.SaleDTO;
import com.restaurant.system.entity.Sale;
import com.restaurant.system.entity.Order;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.SaleRepository;
import com.restaurant.system.repository.OrderRepository;
import com.restaurant.system.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final OrderRepository orderRepository;

    @Override
    public SaleDTO createSale(SaleDTO saleDTO) {
        Order order = orderRepository.findById(saleDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Sale sale = new Sale();
        sale.setOrder(order);
        sale.setTotal(saleDTO.getTotal());
        sale.setPaymentMethod(saleDTO.getPaymentMethod());
        sale.setReceiptNumber(saleDTO.getReceiptNumber());

        return toDTO(saleRepository.save(sale));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SaleDTO> getSaleById(Long id) {
        return saleRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleDTO> getAllSales(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return saleRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SaleDTO> getSaleByOrderId(Long orderId) {
        return saleRepository.findByOrder_Id(orderId).map(this::toDTO);  // ← измени
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SaleDTO> getSalesBetweenDates(OffsetDateTime start, OffsetDateTime end, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return saleRepository.findBySaleTimeBetween(start, end, pageable).map(this::toDTO);
    }

    @Override
    public void deleteSale(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new NotFoundException("Sale not found");
        }
        saleRepository.deleteById(id);
    }

    private SaleDTO toDTO(Sale sale) {
        return SaleDTO.builder()
                .id(sale.getId())
                .saleTime(sale.getSaleTime())
                .total(sale.getTotal())
                .paymentMethod(sale.getPaymentMethod())
                .orderId(sale.getOrder().getId())
                .receiptNumber(sale.getReceiptNumber())
                .processedByUserId(sale.getProcessedBy() != null ? sale.getProcessedBy().getId() : null)
                .build();
    }
}
