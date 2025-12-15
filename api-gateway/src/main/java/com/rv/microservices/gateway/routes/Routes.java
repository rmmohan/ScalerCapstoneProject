package com.rv.microservices.gateway.routes;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {
    
    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return route().GET("/api/product", http())
            .before(uri("http://localhost:8080"))
            .build();
    }
    
    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return route().GET("/api/order", http())
            .before(uri("http://localhost:8081"))
            .build();
    }
    
    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return route().GET("/api/inventory", http())
            .before(uri("http://localhost:8082"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("product_service_swagger")
                .route(path("/aggregate/product-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri("http://localhost:8080"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("order_service_swagger")
                .route(path("/aggregate/order-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("inventory_service_swagger")
                .route(path("/aggregate/inventory-service/v3/api-docs"), http())
                .filter(setPath("/api-docs"))
                .before(uri("http://localhost:8082"))
                .build();
    }
}