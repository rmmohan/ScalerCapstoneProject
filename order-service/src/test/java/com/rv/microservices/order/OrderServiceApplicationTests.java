package com.rv.microservices.order;

import com.rv.microservices.order.dto.OrderRequest;
import com.rv.microservices.order.stub.InventoryClientStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		RestAssured.defaultParser = Parser.JSON;
	}

	@Test
	void shouldCreateOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();

        InventoryClientStub.stubInventoryService(orderRequest.skuCode(), orderRequest.quantity());

		RestAssured.given()
				.contentType("application/json")
				.body(orderRequest)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201);
	}

	private OrderRequest getOrderRequest() {
		return new OrderRequest("sku1234", BigDecimal.valueOf(1200), 1);
	}
}