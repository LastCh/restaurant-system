package com.restaurant.system.controller;

import com.restaurant.system.dto.SupplyDTO;
import com.restaurant.system.dto.SupplyItemDTO;
import com.restaurant.system.entity.enums.SupplyStatus;
import com.restaurant.system.service.SupplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplies")
@RequiredArgsConstructor
@Tag(name = "Supplies", description = "Supply and inventory endpoints")
public class SupplyController {

    private final SupplyService supplyService;

    @PostMapping
    @Operation(summary = "Create a new supply")
    public ResponseEntity<SupplyDTO> createSupply(@Valid @RequestBody SupplyDTO supplyDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplyService.createSupply(supplyDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supply by ID")
    public ResponseEntity<SupplyDTO> getSupplyById(@PathVariable Long id) {
        return supplyService.getSupplyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all supplies with pagination")
    public ResponseEntity<Page<SupplyDTO>> getAllSupplies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "supplyTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(supplyService.getAllSupplies(page, size, sortBy, direction));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get supplies by status")
    public ResponseEntity<Page<SupplyDTO>> getSuppliesByStatus(
            @PathVariable SupplyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "supplyTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(supplyService.getSuppliesByStatus(status, page, size, sortBy, direction));
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get supplies by supplier")
    public ResponseEntity<Page<SupplyDTO>> getSuppliesBySupplierId(
            @PathVariable Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "supplyTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(supplyService.getSuppliesBySupplierId(supplierId, page, size, sortBy, direction));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supply")
    public ResponseEntity<SupplyDTO> updateSupply(
            @PathVariable Long id,
            @Valid @RequestBody SupplyDTO supplyDTO) {
        return ResponseEntity.ok(supplyService.updateSupply(id, supplyDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supply")
    public ResponseEntity<Void> deleteSupply(@PathVariable Long id) {
        supplyService.deleteSupply(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{supplyId}/items")
    @Operation(summary = "Add item to supply")
    public ResponseEntity<SupplyItemDTO> addItemToSupply(
            @PathVariable Long supplyId,
            @Valid @RequestBody SupplyItemDTO itemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplyService.addItemToSupply(supplyId, itemDTO));
    }

    @GetMapping("/{supplyId}/items")
    @Operation(summary = "Get supply items")
    public ResponseEntity<List<SupplyItemDTO>> getSupplyItems(@PathVariable Long supplyId) {
        return ResponseEntity.ok(supplyService.getSupplyItems(supplyId));
    }

    @DeleteMapping("/{supplyId}/items/{itemId}")
    @Operation(summary = "Remove item from supply")
    public ResponseEntity<Void> removeItemFromSupply(
            @PathVariable Long supplyId,
            @PathVariable Long itemId) {
        supplyService.removeItemFromSupply(supplyId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm supply delivery")
    public ResponseEntity<SupplyDTO> confirmSupply(@PathVariable Long id) {
        return ResponseEntity.ok(supplyService.confirmSupply(id));
    }
}