package com.rv.microservices.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
    name = "cashfreeClient",
    url = "${cashfree.base-url}"
)
public interface CashfreeFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/orders", consumes = "application/json")
    Map<String, Object> createOrder(
        @RequestHeader("x-client-id") String clientId,
        @RequestHeader("x-client-secret") String secret,
        @RequestHeader("x-api-version") String apiVersion,
        @RequestBody Map<String, Object> orderPayload
    );

    @RequestMapping(method = RequestMethod.GET, value = "/orders/{orderId}")
    Map<String, Object> getOrder(
            @PathVariable("orderId") String orderId,
            @RequestHeader("x-client-id") String clientId,
            @RequestHeader("x-client-secret") String secretKey,
            @RequestHeader("x-api-version") String apiVersion
    );
}