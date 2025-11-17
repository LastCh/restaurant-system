package com.restaurant.system.service;

import com.restaurant.system.dto.SupplyDTO;
import com.restaurant.system.dto.SupplyItemDTO;
import com.restaurant.system.entity.enums.SupplyStatus;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface SupplyService {
    SupplyDTO createSupply(SupplyDTO supplyDTO);

    Optional<SupplyDTO> getSupplyById(Long id);

    Page<SupplyDTO> getAllSupplies(int page, int size, String sortBy, String direction);

    Page<SupplyDTO> getSuppliesByStatus(SupplyStatus status, int page, int size, String sortBy, String direction);

    Page<SupplyDTO> getSuppliesBySupplierId(Long supplierId, int page, int size, String sortBy, String direction);

    SupplyDTO updateSupply(Long id, SupplyDTO supplyDTO);

    void deleteSupply(Long id);

    SupplyItemDTO addItemToSupply(Long supplyId, SupplyItemDTO itemDTO);

    List<SupplyItemDTO> getSupplyItems(Long supplyId);

    void removeItemFromSupply(Long supplyId, Long itemId);

    SupplyDTO confirmSupply(Long id);
}
