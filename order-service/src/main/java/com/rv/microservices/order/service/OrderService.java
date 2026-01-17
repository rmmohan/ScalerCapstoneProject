package com.rv.microservices.order.service;

import com.rv.microservices.order.event.OrderPlacedEvent;
import com.rv.microservices.order.dto.OrderRequest;
import com.rv.microservices.order.exception.InventoryNotFoundException;
import com.rv.microservices.order.model.Order;
import com.rv.microservices.order.repository.OrderRepository;
import com.rv.microservices.order.client.InventoryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        log.info("Checking inventory for SKU Code: {} and Quantity: {}", orderRequest.skuCode(), orderRequest.quantity());
        boolean inStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (inStock) {
            var order = mapToOrder(orderRequest);
            orderRepository.save(order);

            var orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), orderRequest.userDetails()
                    .email(),
                    orderRequest.userDetails()
                            .firstName(),
                    orderRequest.userDetails()
                            .lastName());
            log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
            ProducerRecord<String, Object> record = new ProducerRecord<>("order-placed", orderPlacedEvent);
            kafkaTemplate.send(record);
            log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
        } else {
            log.error("Product with Skucode {} is not in stock", orderRequest.skuCode());
            throw new InventoryNotFoundException("Product with Skucode " + orderRequest.skuCode() + " is not in stock");
        }
    }

    private static Order mapToOrder(OrderRequest orderRequest) {
        log.info("Mapping OrderRequest to Order entity for SKU Code: {}", orderRequest.skuCode());
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        order.setSkuCode(orderRequest.skuCode());
        return order;
    }
}