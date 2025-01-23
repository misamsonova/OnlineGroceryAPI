package org.samson.onlinegroceryapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(nullable = false)
    private Long id;

    @NotBlank(message = "Product name is required.")
    @Size(max = 255, message = "Product name must not exceed 255 characters.")
    private String name;

    @Size(max = 4096, message = "Description must not exceed 4096 characters.")
    private String description;

    @Min(value = 0, message = "Price must not be less than 0.")
    private double price = 0.0;

    private boolean isAvailable = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
