package com.rv.microservices.product.dto;

import java.math.BigDecimal;

public record ProductRequest(String skuCode, String name, String description, BigDecimal price) {
}