package org.samson.onlinegroceryapi.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
public class Product {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 4096;
    private Long id;
    @NotNull
    @Size(max = MAX_NAME_LENGTH)
    private String name;
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private String description;
    @Min(0)
    private double price;
    private boolean isAvailable = false; // По умолчанию товар не в наличии

    public Product(String name, String description, double price, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    }

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

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
