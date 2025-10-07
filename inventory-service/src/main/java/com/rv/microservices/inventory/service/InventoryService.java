package com.rv.microservices.inventory.service;

import com.rv.microservices.inventory.dto.InventoryRequest;
import com.rv.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(InventoryRequest inventoryRequest) {
        return inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(inventoryRequest.skuCode(), inventoryRequest.quantity());
    }
}