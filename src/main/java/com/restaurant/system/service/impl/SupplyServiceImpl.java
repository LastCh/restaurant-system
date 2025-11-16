package com.restaurant.system.service.impl;

import com.restaurant.system.dto.SupplyDTO;
import com.restaurant.system.dto.SupplyItemDTO;
import com.restaurant.system.entity.Supply;
import com.restaurant.system.entity.SupplyItem;
import com.restaurant.system.entity.Supplier;
import com.restaurant.system.entity.Ingredient;
import com.restaurant.system.entity.enums.SupplyStatus;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.SupplyRepository;
import com.restaurant.system.repository.SupplyItemRepository;
import com.restaurant.system.repository.SupplierRepository;
import com.restaurant.system.repository.IngredientRepository;
import com.restaurant.system.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplyServiceImpl implements SupplyService {

    private final SupplyRepository supplyRepository;
    private final SupplyItemRepository supplyItemRepository;
    private final SupplierRepository supplierRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    public SupplyDTO createSupply(SupplyDTO supplyDTO) {
        Supplier supplier = supplierRepository.findById(supplyDTO.getSupplierId())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        Supply supply = new Supply();
        supply.setSupplier(supplier);
        supply.setStatus(SupplyStatus.PENDING);
        supply.setNotes(supplyDTO.getNotes());

        return toDTO(supplyRepository.save(supply));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplyDTO> getSupplyById(Long id) {
        return supplyRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyDTO> getAllSupplies() {
        return supplyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyDTO> getSuppliesByStatus(SupplyStatus status) {
        return supplyRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyDTO> getSuppliesBySupplierId(Long supplierId) {
        return supplyRepository.findBySupplierId(supplierId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SupplyDTO updateSupply(Long id, SupplyDTO supplyDTO) {
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supply not found"));

        if (supplyDTO.getNotes() != null) {
            supply.setNotes(supplyDTO.getNotes());
        }

        return toDTO(supplyRepository.save(supply));
    }

    @Override
    public void deleteSupply(Long id) {
        if (!supplyRepository.existsById(id)) {
            throw new NotFoundException("Supply not found");
        }
        supplyRepository.deleteById(id);
    }

    @Override
    public SupplyItemDTO addItemToSupply(Long supplyId, SupplyItemDTO itemDTO) {
        Supply supply = supplyRepository.findById(supplyId)
                .orElseThrow(() -> new NotFoundException("Supply not found"));

        Ingredient ingredient = ingredientRepository.findById(itemDTO.getIngredientId())
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        SupplyItem item = new SupplyItem();
        item.setSupply(supply);
        item.setIngredient(ingredient);
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getUnitPrice());

        SupplyItem saved = supplyItemRepository.save(item);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplyItemDTO> getSupplyItems(Long supplyId) {
        return supplyItemRepository.findBySupplyId(supplyId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void removeItemFromSupply(Long supplyId, Long itemId) {
        SupplyItem item = supplyItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Supply item not found"));

        if (!item.getSupply().getId().equals(supplyId)) {
            throw new IllegalArgumentException("Item does not belong to this supply");
        }

        supplyItemRepository.deleteById(itemId);
    }

    @Override
    public SupplyDTO confirmSupply(Long id) {
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supply not found"));

        supply.setStatus(SupplyStatus.CONFIRMED);
        // DB trigger will: add quantities to ingredients, calculate total_cost

        return toDTO(supplyRepository.save(supply));
    }

    private SupplyDTO toDTO(Supply supply) {
        List<SupplyItemDTO> items = supplyItemRepository.findBySupplyId(supply.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return SupplyDTO.builder()
                .id(supply.getId())
                .supplyTime(supply.getSupplyTime())
                .supplierId(supply.getSupplier() != null ? supply.getSupplier().getId() : null)
                .supplierName(supply.getSupplier() != null ? supply.getSupplier().getName() : null)
                .status(supply.getStatus())
                .totalCost(supply.getTotalCost())
                .notes(supply.getNotes())
                .receivedByUserId(supply.getReceivedBy() != null ? supply.getReceivedBy().getId() : null)
                .createdAt(supply.getCreatedAt())
                .items(items)
                .build();
    }

    private SupplyItemDTO toDTO(SupplyItem item) {
        return SupplyItemDTO.builder()
                .id(item.getId())
                .supplyId(item.getSupply().getId())
                .ingredientId(item.getIngredient().getId())
                .ingredientName(item.getIngredient().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}
