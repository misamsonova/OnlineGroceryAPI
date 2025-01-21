package com.samson.onlinegroceryapi.controller;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.samson.OnlineGroceryApiApplication;
import org.samson.onlinegroceryapi.controller.ProductController;
import org.samson.onlinegroceryapi.model.Product;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)  // Аннотация для запуска Spring тестов
@WebMvcTest(ProductController.class)  // Тестируем только веб-слой (контроллеры)
@ContextConfiguration(classes = OnlineGroceryApiApplication.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;  // Мок для тестирования контроллера

    @MockBean
    private ProductService productService;  // Мок для ProductService

    @Test
    public void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(
                new Product("Product 1", "Description 1", 100.0, true),
                new Product("Product 2", "Description 2", 50.0, false)
        );

        // Мокаем поведение сервиса
        given(productService.getAllProducts()).willReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Product 1")))
                .andExpect(jsonPath("$[1].name", is("Product 2")));
    }

    @Test
    public void testGetProductById() throws Exception {
        Product product = new Product("Product 1", "Description 1", 100.0, true);
        given(productService.getProductById(1L)).willReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Product 1")));
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product product = new Product("Product 1", "Description 1", 100.0, true);
        given(productService.createProduct(any(Product.class))).willReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Product 1\",\"description\":\"Description 1\",\"price\":100.0,\"available\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Product 1")));
    }

    @Test
    public void testUpdateProduct() throws Exception {
//        // Старый продукт
//        Product existingProduct = new Product("Product 1", "Description 1", 100.0, true);

        // Новый обновленный продукт
        Product updatedProduct = new Product("Updated Product 1", "Updated Description 1", 120.0, false);

        // Мокаем поведение сервиса
        given(productService.updateProduct(anyLong(), any(Product.class))).willReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Product 1\",\"description\":\"Updated Description 1\",\"price\":120.0,\"available\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product 1")));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk());
    }
}
