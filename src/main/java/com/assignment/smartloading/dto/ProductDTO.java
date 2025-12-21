package com.assignment.smartloading.dto;

import com.assignment.smartloading.model.Product;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String productId;
    private final String productName;
    private final String category;
    private final String description;
    private final double price;
    private final String imageUrl;

    @JsonCreator
    public ProductDTO(
            @JsonProperty("productId") String productId,
            @JsonProperty("productName") String productName,
            @JsonProperty("category") String category,
            @JsonProperty("description") String description,
            @JsonProperty("price") double price,
            @JsonProperty("imageUrl") String imageUrl
    ) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static ProductDTO from(Product p) {
        return new ProductDTO(
                p.getProductId(),
                p.getProductName(),
                p.getCategory(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl()
        );
    }

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }

    @Override
    public String toString() {
        return productId + ":" + productName;
    }
}
