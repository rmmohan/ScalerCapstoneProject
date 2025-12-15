package com.rv.microservices.order.exception;

/**
 * Runtime exception thrown when referenced inventory items are not found while placing an order.
 */
public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String message) {
        super(message);
    }

    public InventoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}