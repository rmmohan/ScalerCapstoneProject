package com.rv.microservices.inventory.service;

import com.rv.microservices.inventory.dto.InventoryRequest;
import com.rv.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(InventoryRequest inventoryRequest) {
        log.info("Checking inventory for SKU Code: {} and Quantity: {}",
                inventoryRequest.skuCode(), inventoryRequest.quantity());
        return inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(inventoryRequest.skuCode(), inventoryRequest.quantity());
    }
}