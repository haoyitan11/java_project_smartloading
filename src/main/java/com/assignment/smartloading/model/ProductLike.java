package com.assignment.smartloading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_likes")
public class ProductLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String productId;

    public ProductLike() {}

    public ProductLike(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }

    // GETTERS & SETTERS
    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
