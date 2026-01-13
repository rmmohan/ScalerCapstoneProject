package com.rv.microservices.gateway.routes;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Slf4j
@Configuration
public class Routes {
    
    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        log.info("Routing for Product Service initialized");
        return GatewayRouterFunctions.route("product_service")
                .route(path("/api/product"), http())
                .before(uri("http://product-service:8080"))
                .filter(circuitBreaker("productServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        log.info("Routing for Order Service initialized");
        return GatewayRouterFunctions.route("order_service")
                .route(path("/api/order"), http())
                .before(uri("http://order-service:8080"))
                .filter(circuitBreaker("orderServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }
    
    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        log.info("Routing for Inventory Service initialized");
        return GatewayRouterFunctions.route("inventory_service")
                .route(path("/api/inventory"), http())
                .before(uri("http://inventory-service:8080"))
                .filter(circuitBreaker("inventoryServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        log.info("Routing for Product Service Swagger initialized");
        return GatewayRouterFunctions.route("product_service_swagger")
                .route(path("/aggregate/product-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri("http://product-service:8080"))
                .filter(circuitBreaker("productServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        log.info("Routing for Order Service Swagger initialized");
        return GatewayRouterFunctions.route("order_service_swagger")
                .route(path("/aggregate/order-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri("http://order-service:8080"))
                .filter(circuitBreaker("orderServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        log.info("Routing for Inventory Service Swagger initialized");
        return GatewayRouterFunctions.route("inventory_service_swagger")
                .route(path("/aggregate/inventory-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri("http://inventory-service:8080"))
                .filter(circuitBreaker("inventoryServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }
    
    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        log.info("Fallback route initialized");
        return GatewayRouterFunctions.route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service Unavailable, please try again later"))
                .build();
    }
}