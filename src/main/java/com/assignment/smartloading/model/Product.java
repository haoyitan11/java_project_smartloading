package com.assignment.smartloading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;

    private String category;
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private double price;

    @Column(name = "product_name")
    private String productName;

    // ---------- NEW FIELDS (NOT STORED IN DB) ----------
    @Transient
    private boolean likedByUser;

    @Transient
    private long likes;

    // ---------- GETTERS & SETTERS ----------
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public boolean isLikedByUser() { return likedByUser; }
    public void setLikedByUser(boolean likedByUser) { this.likedByUser = likedByUser; }

    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }
}
