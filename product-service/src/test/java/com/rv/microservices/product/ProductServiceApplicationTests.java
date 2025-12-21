package com.rv.microservices.product;

import com.rv.microservices.product.dto.ProductRequest;
import com.rv.microservices.product.dto.ProductResponse;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class ProductServiceApplicationTests {

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();

		RestAssured.given()
				.contentType("application/json")
				.body(productRequest)
				.when()
				.post("/api/product")
				.then()
				.log().all()
				.statusCode(201);
	}

	@Test
	void shouldGetAllProducts() {
		ProductRequest productRequest = getProductRequest();

		List<ProductResponse> products = RestAssured.given()
				.get("/api/product")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getList(".", ProductResponse.class);

		// Verify list size and name
		org.junit.jupiter.api.Assertions.assertEquals(1, products.size());
        org.junit.jupiter.api.Assertions.assertEquals(productRequest.skuCode(), products.get(0).skuCode());
        org.junit.jupiter.api.Assertions.assertEquals(productRequest.name(), products.get(0).name());
		org.junit.jupiter.api.Assertions.assertEquals(productRequest.description(), products.get(0).description());
		org.junit.jupiter.api.Assertions.assertEquals(productRequest.price().intValueExact(), products.get(0).price().intValueExact());
	}

	private ProductRequest getProductRequest() {
		return new ProductRequest("iPhone 13", "iPhone 13", "iPhone 13", BigDecimal.valueOf(1200));
	}
}