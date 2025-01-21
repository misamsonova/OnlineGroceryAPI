package org.samson.onlinegroceryapi.service;

import org.samson.onlinegroceryapi.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();
    private Long idCounter = 1L;

    public List<Product> getAllProducts(){
        return new ArrayList<>(products);
    }

    public Product createProduct(Product product){
        products.add(product);
        product.setId(idCounter++);
        return product;
    }
    public Product updateProduct(Long id, Product product){
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setAvailable(product.isAvailable());
        return existingProduct;
    }

    public void deleteProduct(Long id){
        products.removeIf(product -> product.getId().equals(id));
    }

    public Product getProductById(Long id){
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

}
