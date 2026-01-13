package com.rv.microservices.order.controller;

import com.rv.microservices.order.dto.OrderRequest;
import com.rv.microservices.order.exception.InventoryNotFoundException;
import com.rv.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            log.info("Received request to place order: {}", orderRequest);
            orderService.placeOrder(orderRequest);
            log.info("Order placed successfully for request: {}", orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Placed Successfully");
        } catch (InventoryNotFoundException ex) {
            log.error("Error placing order: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}