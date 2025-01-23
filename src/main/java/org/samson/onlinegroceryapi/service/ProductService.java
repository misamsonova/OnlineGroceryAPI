package org.samson.onlinegroceryapi.service;

import org.samson.onlinegroceryapi.dto.ProductDTO;
import org.samson.onlinegroceryapi.exception.ResourceNotFoundException;
import org.samson.onlinegroceryapi.model.Product;
import org.samson.onlinegroceryapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.PriorityQueue;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PriorityQueue<Long> availableIds = new PriorityQueue<>();
    private Long nextId = 1L;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        initializeAvailableIds();
    }

    private void initializeAvailableIds() {
        List<Long> existingIds = productRepository.findAll()
                .stream()
                .map(Product::getId)
                .sorted()
                .toList();

        long expectedId = 1L;

        for (Long existingId : existingIds) {
            while (expectedId < existingId) {
                availableIds.add(expectedId);
                expectedId++;
            }
            expectedId = existingId + 1;
        }

        nextId = Math.max(nextId, expectedId);
    }

    public List<Long> getAvailableIds() {
        return new ArrayList<>(availableIds);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(ProductDTO::getId))
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
        return convertToDto(product);
    }

    public ProductDTO createProduct(ProductDTO productDto) {
        Product product = convertToEntity(productDto);

        Long id;
        if (!availableIds.isEmpty()) {
            id = availableIds.poll();
        } else {
            id = nextId++;
        }
        product.setId(id);

        Product savedProduct = productRepository.save(product);

        updateAvailableIds();

        return convertToDto(savedProduct);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
        productRepository.delete(product);
        availableIds.add(id);
    }


    private void updateAvailableIds() {
        List<Long> existingIds = productRepository.findAll()
                .stream()
                .map(Product::getId)
                .toList();

        long expectedId = 1L;

        while (expectedId < nextId) {
            if (!existingIds.contains(expectedId) && !availableIds.contains(expectedId)) {
                availableIds.add(expectedId);
            }
            expectedId++;
        }
    }

    private ProductDTO convertToDto(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setIsAvailable(product.isAvailable());
        return productDTO;
    }

    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setIsAvailable(productDTO.isAvailable());
        return product;
    }
}
