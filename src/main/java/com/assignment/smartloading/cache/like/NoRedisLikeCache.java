package com.assignment.smartloading.cache.like;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("local")
public class NoRedisLikeCache implements LikeCache {
    @Override public boolean hasUserLikesKey(String userId) { return false; }
    @Override public Set<String> getUserLikes(String userId) { return Collections.emptySet(); }
    @Override public void cacheUserLikes(String userId, Set<String> productIds) {}
    @Override public void evictUserLikes(String userId) {}

    @Override public Long getProductLikesCount(String productId) { return null; }
    @Override public void cacheProductLikesCount(String productId, long count) {}

    @Override public List<Long> getProductLikesCounts(List<String> productIds) {
        return Collections.nCopies(productIds == null ? 0 : productIds.size(), null);
    }
}
