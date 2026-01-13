package com.rv.microservices.inventory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rv.microservices.inventory.dto.InventoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

    @LocalServerPort
    private Integer port;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
    }

//    @Test
//    void shouldCheckIfInventoryExists() throws Exception {
//        InventoryRequest inventoryRequest = new InventoryRequest("iphone_15", 100);
//
//        Map<String, Object> params = mapper.convertValue(
//            inventoryRequest, new TypeReference<Map<String, Object>>() {}
//        );
//
//        Boolean response = RestAssured.given()
//                .queryParams(params)
//                .when()
//                .get("/api/inventory")
//                .then()
//                .log().all()
//                .statusCode(200)
//                .extract()
//                .as(Boolean.class);
//
//        assertTrue(response);
//    }

//    @Test
//    void shouldCheckIfInventoryDoesNotExist() throws Exception {
//        InventoryRequest inventoryRequest = new InventoryRequest("iphone_15", 101);
//
//        Map<String, Object> params = mapper.convertValue(
//                inventoryRequest, new TypeReference<Map<String, Object>>() {}
//        );
//
//        Boolean response = RestAssured.given()
//                .queryParams(params)
//                .when()
//                .get("/api/inventory")
//                .then()
//                .log().all()
//                .statusCode(200)
//                .extract()
//                .as(Boolean.class);
//
//        assertFalse(response);
//    }
}