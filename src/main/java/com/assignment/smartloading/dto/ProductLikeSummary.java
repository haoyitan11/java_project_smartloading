package com.assignment.smartloading.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ProductLikeSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String productId;
    private final long totalLikes;
    private final boolean likedByUser;

    @JsonCreator
    public ProductLikeSummary(
            @JsonProperty("productId") String productId,
            @JsonProperty("totalLikes") long totalLikes,
            @JsonProperty("likedByUser") boolean likedByUser
    ) {
        this.productId = productId;
        this.totalLikes = totalLikes;
        this.likedByUser = likedByUser;
    }

    public String getProductId() { return productId; }
    public long getTotalLikes() { return totalLikes; }
    public boolean isLikedByUser() { return likedByUser; }
}
