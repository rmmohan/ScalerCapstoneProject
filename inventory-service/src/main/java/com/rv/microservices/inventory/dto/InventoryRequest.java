package com.rv.microservices.inventory.dto;

import java.io.Serializable;

public record InventoryRequest(String skuCode, Integer quantity) implements Serializable {
}