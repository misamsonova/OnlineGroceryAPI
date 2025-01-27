package org.samson.onlinegroceryapi.service;

import jakarta.persistence.criteria.Predicate;
import org.samson.onlinegroceryapi.dto.ProductDTO;
import org.samson.onlinegroceryapi.exception.ResourceNotFoundException;
import org.samson.onlinegroceryapi.model.Product;
import org.samson.onlinegroceryapi.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

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


    public List<ProductDTO> getFilteredProducts(String name, Double priceMin, Double priceMax, Boolean isAvailable,
                                                String sortBy, Integer limit) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (priceMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceMin));
            }
            if (priceMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceMax));
            }
            if (isAvailable != null) {
                predicates.add(criteriaBuilder.equal(root.get("isAvailable"), isAvailable));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.ASC, sortBy != null ? sortBy : "id");
        PageRequest pageRequest = PageRequest.of(0, limit != null ? limit : Integer.MAX_VALUE, sort);

        return productRepository.findAll(specification, pageRequest)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
        Product savedProduct = productRepository.save(product);
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
