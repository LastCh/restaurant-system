package com.restaurant.system.service;

import com.restaurant.system.dto.SupplyDTO;
import com.restaurant.system.dto.SupplyItemDTO;
import com.restaurant.system.entity.enums.SupplyStatus;
import java.util.List;
import java.util.Optional;

public interface SupplyService {
    SupplyDTO createSupply(SupplyDTO supplyDTO);
    Optional<SupplyDTO> getSupplyById(Long id);
    List<SupplyDTO> getAllSupplies();
    List<SupplyDTO> getSuppliesByStatus(SupplyStatus status);
    List<SupplyDTO> getSuppliesBySupplierId(Long supplierId);
    SupplyDTO updateSupply(Long id, SupplyDTO supplyDTO);
    void deleteSupply(Long id);

    SupplyItemDTO addItemToSupply(Long supplyId, SupplyItemDTO itemDTO);
    List<SupplyItemDTO> getSupplyItems(Long supplyId);
    void removeItemFromSupply(Long supplyId, Long itemId);
    SupplyDTO confirmSupply(Long id);
}
