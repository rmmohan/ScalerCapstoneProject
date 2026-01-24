package com.rv.microservices.order.dto;

import java.math.BigDecimal;

public record OrderRequest(String skuCode, BigDecimal price, Integer quantity, String returnUrl, UserDetails userDetails) {

}