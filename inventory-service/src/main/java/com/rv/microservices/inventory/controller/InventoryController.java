package com.rv.microservices.inventory.controller;

import com.rv.microservices.inventory.dto.InventoryRequest;
import com.rv.microservices.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@ModelAttribute InventoryRequest inventoryRequest) {
        return inventoryService.isInStock(inventoryRequest);
    }
}