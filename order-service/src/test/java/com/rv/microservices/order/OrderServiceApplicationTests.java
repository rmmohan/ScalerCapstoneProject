package com.rv.microservices.order;

import com.rv.microservices.order.client.InventoryClient;
import com.rv.microservices.order.dto.OrderRequest;
import com.rv.microservices.order.dto.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureStubRunner(
    ids = "com.rv.microservices:inventory-service:+:stubs",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test"
)
@Import(TestcontainersConfiguration.class)
class OrderServiceApplicationTests {

	@LocalServerPort
	private Integer port;

    @StubRunnerPort("inventory-service")
    private int inventoryServicePort;

    @Autowired
    private InventoryClient inventoryClient;

    @BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		RestAssured.defaultParser = Parser.JSON;
        System.setProperty("inventory.url", "http://localhost:" + inventoryServicePort);
	}

//    @Test
//    void shouldReturnTrueForAvailableProduct() {
//        OrderRequest orderRequest = getOrderRequest("oneplus_12", 25);
//        boolean inStock =
//                inventoryClient.isInStock("oneplus_12", 25);
//
//        RestAssured.given()
//                .contentType("application/json")
//                .body(orderRequest)
//                .when()
//                .post("/api/order")
//                .then()
//                .log().all()
//                .statusCode(201);
//
//        assertThat(inStock).isTrue();
//    }
//
//    @Test
//    void shouldReturnFalseForUnavailableProduct() {
//        OrderRequest orderRequest = getOrderRequest("pixel_8", 101);
//        boolean inStock =
//                inventoryClient.isInStock("pixel_8", 101);
//
//        RestAssured.given()
//                .contentType("application/json")
//                .body(orderRequest)
//                .when()
//                .post("/api/order")
//                .then()
//                .log().all()
//                .statusCode(201);
//
//        assertThat(inStock).isFalse();
//    }

	private OrderRequest getOrderRequest(String skuCode, Integer quantity) {
        String returnUrl = "http://localhost:8080/api/order/status/";
        UserDetails userDetails = new UserDetails("johndoe", "John", "Doe", "test@example.com", "0000000000");
		return new OrderRequest(skuCode, BigDecimal.valueOf(1200), quantity, returnUrl, userDetails);
	}
}