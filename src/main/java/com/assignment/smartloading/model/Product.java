package com.assignment.smartloading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")   //FIXED — must match PostgreSQL table name
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    private String category;

    private String description;

    private double price;

    @Column(name = "image_url")
    private String imageUrl;

    // --- getters and setters ---
    public String getProductId() { return productId; }
    public void setProductId(String id) { this.productId = id; }

    public String getProductName() { return productName; }
    public void setProductName(String name) { this.productName = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public double getPrice() { return price; }
    public void setPrice(double p) { this.price = p; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String url) { this.imageUrl = url; }
}