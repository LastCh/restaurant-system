package com.restaurant.system.controller;

import com.restaurant.system.dto.SupplyDTO;
import com.restaurant.system.dto.SupplyItemDTO;
import com.restaurant.system.entity.enums.SupplyStatus;
import com.restaurant.system.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplies")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService supplyService;

    @PostMapping
    public ResponseEntity<SupplyDTO> createSupply(@RequestBody SupplyDTO supplyDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplyService.createSupply(supplyDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyDTO> getSupplyById(@PathVariable Long id) {
        return supplyService.getSupplyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SupplyDTO>> getAllSupplies() {
        return ResponseEntity.ok(supplyService.getAllSupplies());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupplyDTO>> getSuppliesByStatus(@PathVariable SupplyStatus status) {
        return ResponseEntity.ok(supplyService.getSuppliesByStatus(status));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<SupplyDTO>> getSuppliesBySupplierId(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplyService.getSuppliesBySupplierId(supplierId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplyDTO> updateSupply(
            @PathVariable Long id,
            @RequestBody SupplyDTO supplyDTO) {
        return ResponseEntity.ok(supplyService.updateSupply(id, supplyDTO));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<SupplyDTO> confirmSupply(@PathVariable Long id) {
        return ResponseEntity.ok(supplyService.confirmSupply(id));
    }

    @PostMapping("/{supplyId}/items")
    public ResponseEntity<SupplyItemDTO> addItemToSupply(
            @PathVariable Long supplyId,
            @RequestBody SupplyItemDTO itemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplyService.addItemToSupply(supplyId, itemDTO));
    }

    @GetMapping("/{supplyId}/items")
    public ResponseEntity<List<SupplyItemDTO>> getSupplyItems(@PathVariable Long supplyId) {
        return ResponseEntity.ok(supplyService.getSupplyItems(supplyId));
    }

    @DeleteMapping("/{supplyId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromSupply(
            @PathVariable Long supplyId,
            @PathVariable Long itemId) {
        supplyService.removeItemFromSupply(supplyId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupply(@PathVariable Long id) {
        supplyService.deleteSupply(id);
        return ResponseEntity.noContent().build();
    }
}
