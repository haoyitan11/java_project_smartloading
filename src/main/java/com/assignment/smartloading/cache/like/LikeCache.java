package com.assignment.smartloading.cache.like;

import java.util.List;
import java.util.Set;

public interface LikeCache {
    boolean hasUserLikesKey(String userId);
    Set<String> getUserLikes(String userId);
    void cacheUserLikes(String userId, Set<String> productIds);
    void evictUserLikes(String userId);

    Long getProductLikesCount(String productId);
    void cacheProductLikesCount(String productId, long count);

    List<Long> getProductLikesCounts(List<String> productIds);
}
