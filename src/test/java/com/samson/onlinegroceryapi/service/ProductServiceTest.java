package com.samson.onlinegroceryapi.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.samson.onlinegroceryapi.dto.ProductDTO;
import org.samson.onlinegroceryapi.exception.ResourceNotFoundException;
import org.samson.onlinegroceryapi.model.Product;
import org.samson.onlinegroceryapi.repository.ProductRepository;
import org.samson.onlinegroceryapi.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private AutoCloseable mocks;
    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void testInitializeAvailableIds() {
        List<Product> products = new ArrayList<>();
        products.add(createProduct(2L));
        products.add(createProduct(4L));
        products.add(createProduct(6L));

        when(productRepository.findAll()).thenReturn(products);

        productService = new ProductService(productRepository);

        List<Long> availableIds = productService.getAvailableIds();

        assertEquals(List.of(1L, 3L, 5L), availableIds);
    }

    @Test
    void testCreateProductUsesAvailableId() {
        List<Product> products = new ArrayList<>();
        products.add(createProduct(2L));
        when(productRepository.findAll()).thenReturn(products);

        productService = new ProductService(productRepository);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO createdProduct = productService.createProduct(productDTO);

        assertEquals(1L, createdProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProductAddsIdToAvailable() {
        Product product = createProduct(5L);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        productService.deleteProduct(5L);

        List<Long> availableIds = productService.getAvailableIds();
        assertTrue(availableIds.contains(5L));
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testGetProductById() {
        Product product = createProduct(3L);
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        ProductDTO productDTO = productService.getProductById(3L);

        assertEquals(3L, productDTO.getId());
        assertEquals("Product 3", productDTO.getName());
        verify(productRepository, times(1)).findById(3L);
    }

    @Test
    void testUpdateProduct() {
        Product product = createProduct(3L);
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setPrice(20.0);

        product.setName(updateDTO.getName());
        product.setPrice(updateDTO.getPrice());

        when(productRepository.save(product)).thenReturn(product);

        ProductDTO updatedProduct = productService.updateProduct(3L, updateDTO);

        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(20.0, updatedProduct.getPrice());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(createProduct(1L));
        products.add(createProduct(2L));
        when(productRepository.findAll()).thenReturn(products);

        List<ProductDTO> allProducts = productService.getAllProducts();

        assertEquals(2, allProducts.size());
        assertEquals(1L, allProducts.get(0).getId());
        assertEquals(2L, allProducts.get(1).getId());
        verify(productRepository, times(2)).findAll();
    }

    @Test
    void testGetProductByIdThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    private Product createProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product " + id);
        product.setPrice(10.0);
        product.setIsAvailable(true);
        return product;
    }
}