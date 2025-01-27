package com.samson.onlinegroceryapi.controller;

import org.junit.runner.RunWith;
import org.samson.OnlineGroceryApiApplication;
import org.samson.onlinegroceryapi.controller.ProductController;
import org.samson.onlinegroceryapi.dto.ProductDTO;
import org.samson.onlinegroceryapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@RunWith(SpringRunner.class)  // Аннотация для запуска Spring тестов
@WebMvcTest(ProductController.class)  // Тестируем только веб-слой (контроллеры)
@ContextConfiguration(classes = OnlineGroceryApiApplication.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductDTO product1;
    private ProductDTO product2;

    @BeforeEach
    void setUp() {
        product1 = new ProductDTO();
        product1.setId(1L);
        product1.setName("Apple");
        product1.setDescription("Fresh red apple");
        product1.setPrice(1.2);
        product1.setIsAvailable(true);

        product2 = new ProductDTO();
        product2.setId(2L);
        product2.setName("Banana");
        product2.setDescription("Ripe yellow banana");
        product2.setPrice(0.8);
        product2.setIsAvailable(true);

    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        given(productService.getAllProducts()).willReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Banana"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        given(productService.getProductById(1L)).willReturn(product1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product1.getId()))
                .andExpect(jsonPath("$.name").value(product1.getName()));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        given(productService.createProduct(Mockito.any(ProductDTO.class))).willReturn(product1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product1.getId()))
                .andExpect(jsonPath("$.name").value(product1.getName()));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        given(productService.updateProduct(Mockito.eq(1L), Mockito.any(ProductDTO.class))).willReturn(product1);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product1.getId()))
                .andExpect(jsonPath("$.name").value(product1.getName()));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productService).deleteProduct(1L);
    }

    @Test
    void getFilteredProducts_ShouldReturnFilteredList() throws Exception {
        given(productService.getFilteredProducts("Apple", null, null, true, "name", 10))
                .willReturn(Collections.singletonList(product1));

        mockMvc.perform(get("/api/products/filtered")
                        .param("name", "Apple")
                        .param("isAvailable", "true")
                        .param("limit", "10")
                        .param("sortBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Apple"));
    }
}