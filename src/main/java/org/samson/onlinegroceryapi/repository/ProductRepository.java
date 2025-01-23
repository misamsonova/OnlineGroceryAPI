package org.samson.onlinegroceryapi.repository;

import org.samson.onlinegroceryapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}