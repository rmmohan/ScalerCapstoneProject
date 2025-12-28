package com.rv.microservices.inventory.contract;

import com.rv.microservices.inventory.InventoryServiceApplication;
import com.rv.microservices.inventory.dto.InventoryRequest;
import com.rv.microservices.inventory.service.InventoryService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = InventoryServiceApplication.class, properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
public abstract class InventoryBaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        when(inventoryService.isInStock(
            new InventoryRequest("oneplus_12", 25)
        )).thenReturn(true);

        when(inventoryService.isInStock(
            new InventoryRequest("pixel_8", 101)
        )).thenReturn(false);
    }
}