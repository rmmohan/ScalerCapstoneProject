package com.rv.microservices.order.controller;

import com.rv.microservices.order.dto.OrderRequest;
import com.rv.microservices.order.exception.InventoryNotFoundException;
import com.rv.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            log.info("Received request to place order: {}", orderRequest);
            Map<String, Object> response = orderService.placeOrder(orderRequest);
            log.info("Order placed successfully for request: {}", orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InventoryNotFoundException ex) {
            log.error("Error placing order: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> checkStatus(@PathVariable String orderId) {
        log.info("Received request to check status for orderId: {}", orderId);
        Map<String, Object> result = orderService.getOrderStatus(orderId);

        Object status = result.get("order_status");
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("cashfreeStatus", status);
        return ResponseEntity.ok(response);
    }
}