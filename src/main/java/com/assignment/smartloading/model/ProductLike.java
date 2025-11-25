package com.assignment.smartloading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_likes",
        indexes = {
                @Index(name = "idx_user_product", columnList = "user_id, product_id", unique = true)
        })
public class ProductLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    public ProductLike() {}

    public ProductLike(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public Long getId() { return id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
}
