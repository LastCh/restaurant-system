package com.restaurant.system.controller;

import com.restaurant.system.dto.SaleDTO;
import com.restaurant.system.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sale and payment endpoints")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @Operation(summary = "Create a new sale")
    public ResponseEntity<SaleDTO> createSale(@Valid @RequestBody SaleDTO saleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saleService.createSale(saleDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all sales with pagination")
    public ResponseEntity<Page<SaleDTO>> getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(saleService.getAllSales(page, size, sortBy, direction));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get sale by order ID")
    public ResponseEntity<SaleDTO> getSaleByOrderId(@PathVariable Long orderId) {
        return saleService.getSaleByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/between")
    @Operation(summary = "Get sales between dates")
    public ResponseEntity<Page<SaleDTO>> getSalesBetweenDates(
            @RequestParam OffsetDateTime start,
            @RequestParam OffsetDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(saleService.getSalesBetweenDates(start, end, page, size, sortBy, direction));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sale")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}