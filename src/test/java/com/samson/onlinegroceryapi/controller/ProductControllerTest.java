package com.samson.onlinegroceryapi.controller;

import org.junit.runner.RunWith;
//import org.junit.Test;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)  // Аннотация для запуска Spring тестов
@WebMvcTest(ProductController.class)  // Тестируем только веб-слой (контроллеры)
@ContextConfiguration(classes = OnlineGroceryApiApplication.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

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
        List<ProductDTO> products = Arrays.asList(product1, product2);
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(product1.getId()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[1].id").value(product2.getId()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        Mockito.when(productService.getProductById(1L)).thenReturn(product1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product1.getId()))
                .andExpect(jsonPath("$.name").value(product1.getName()));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        Mockito.when(productService.createProduct(any(ProductDTO.class))).thenReturn(product1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product1.getId()))
                .andExpect(jsonPath("$.name").value(product1.getName()));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        Mockito.when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(product1);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
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
}
