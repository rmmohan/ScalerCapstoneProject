package com.rv.microservices.order.service;

import com.rv.microservices.order.client.CashfreeFeignClient;
import com.rv.microservices.order.dto.UserDetails;
import com.rv.microservices.order.event.OrderPlacedEvent;
import com.rv.microservices.order.dto.OrderRequest;
import com.rv.microservices.order.exception.InventoryNotFoundException;
import com.rv.microservices.order.model.Order;
import com.rv.microservices.order.model.PaymentStatus;
import com.rv.microservices.order.repository.OrderRepository;
import com.rv.microservices.order.client.InventoryClient;
import com.rv.microservices.order.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    @Value("${cashfree.client-id}")
    private String clientId;

    @Value("${cashfree.client-secret}")
    private String secretKey;

    @Value("${cashfree.api-version}")
    private String apiVersion;

    private final InventoryClient inventoryClient;
    private final CashfreeFeignClient cashfreeFeignClient;
    private final OrderRepository orderRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Map<String, Object> placeOrder(OrderRequest orderRequest) {
        log.info("Checking inventory for SKU Code: {} and Quantity: {}", orderRequest.skuCode(), orderRequest.quantity());
        boolean inStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (inStock) {
            Order order = mapToOrder(orderRequest);
            orderRepository.save(order);

            log.info("Creating order in Cashfree for Order Number: {}", order.getOrderNumber());
            Map<String, Object> orderPayload = mapToOrderPayload(order, orderRequest);
            return cashfreeFeignClient.createOrder(clientId, secretKey, apiVersion, orderPayload);
        } else {
            log.error("Product with Skucode {} is not in stock", orderRequest.skuCode());
            throw new InventoryNotFoundException("Product with Skucode " + orderRequest.skuCode() + " is not in stock");
        }
    }

    public Map<String, Object> getOrderStatus(String orderId) {
        log.info("Fetching order status from Cashfree for Order ID: {}", orderId);
        Map<String, Object> result =  cashfreeFeignClient.getOrder(orderId, clientId, secretKey, apiVersion);
        log.info("Order status received from Cashfree for Order ID: {}: {}", orderId, result);
        if(result.containsKey("order_status")) {
            log.info("Updating local order status for Order ID: {}: {}", orderId, result.get("order_status"));
            if(result.get("order_status").toString().equals("PAID")) {
                Order order = orderRepository.findByOrderNumber(orderId);
                PaymentStatus paidStatus = paymentStatusRepository.findByStatus("COMPLETED");
                order.setPaymentStatus(paidStatus);
                orderRepository.save(order);
                log.info("Local order status updated to COMPLETED for Order ID: {}", orderId);
                notifyUser(order);
                log.info("User notified for Order ID: {}", orderId);
            }
        }
        return result;
    }

    private void notifyUser(Order order) {
        var orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), order.getEmail(),
                order.getFirstName(),
                order.getLastName());
        log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
        ProducerRecord<String, Object> record = new ProducerRecord<>("order-placed", orderPlacedEvent);
        kafkaTemplate.send(record);
        log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
    }

    private Order mapToOrder(OrderRequest orderRequest) {
        log.info("Mapping OrderRequest to Order entity for SKU Code: {}", orderRequest.skuCode());
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setFirstName(orderRequest.userDetails().firstName());
        order.setLastName(orderRequest.userDetails().lastName());
        order.setEmail(orderRequest.userDetails().email());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        order.setSkuCode(orderRequest.skuCode());

        PaymentStatus paymentStatus = paymentStatusRepository.findByStatus("PENDING");
        order.setPaymentStatus(paymentStatus);
        return order;
    }

    private Map<String, Object> mapToOrderPayload(Order order, OrderRequest orderRequest) {
        UserDetails userDetails = orderRequest.userDetails();
        return Map.of(
            "order_id", order.getOrderNumber(),
            "order_amount", order.getPrice(),
            "order_currency", "INR",
            "customer_details", Map.of(
                "customer_id", userDetails.username(),
                "customer_phone", userDetails.phoneNumber(),
                "customer_name", userDetails.firstName() + " " + userDetails.lastName(),
                "customer_email", userDetails.email()
                ),
            "order_meta", Map.of(
                    "return_url", orderRequest.returnUrl(),
                    "payment_methods", "upi"
//                    "notify_url", "http://api-gateway:9000/api/order/cashfree/webhook"
                )
        );
    }
}