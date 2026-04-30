package com.ecommerce.product_service;

import com.ecommerce.product_service.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        Product newProduct = new Product(null, "Test prsten", "Test opis",
                new BigDecimal("99.99"), 10, "Prstenje");

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test prsten"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void getProductsByCategory_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/products/category/Prstenje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}