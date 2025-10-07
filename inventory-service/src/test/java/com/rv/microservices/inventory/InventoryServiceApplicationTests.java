package com.rv.microservices.inventory;

import com.rv.microservices.inventory.dto.InventoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    void shouldCheckIfInventoryExists() throws Exception {
        InventoryRequest inventoryRequest = new InventoryRequest("iphone_15", 100);

        Boolean response = RestAssured.given()
                .contentType("application/json")
                .body(inventoryRequest)
                .when()
                .get("/api/inventory")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(Boolean.class);

        assertTrue(response);
    }

    @Test
    void shouldCheckIfInventoryDoesNotExist() throws Exception {
        InventoryRequest inventoryRequest = new InventoryRequest("iphone_15", 101);

        Boolean response = RestAssured.given()
                .contentType("application/json")
                .body(inventoryRequest)
                .when()
                .get("/api/inventory")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(Boolean.class);

        assertFalse(response);
    }
}