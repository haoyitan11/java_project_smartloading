package com.assignment.smartloading.cache.like;

import com.assignment.smartloading.cache.RedisKeyUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Profile("k8s")
public class RedisLikeCache implements LikeCache {

    private final StringRedisTemplate srt;

    public RedisLikeCache(StringRedisTemplate srt) {
        this.srt = srt;
    }

    @Override
    public boolean hasUserLikesKey(String userId) {
        Boolean exists = srt.hasKey(RedisKeyUtil.userLikes(userId));
        return exists != null && exists;
    }

    @Override
    public Set<String> getUserLikes(String userId) {
        return srt.opsForSet().members(RedisKeyUtil.userLikes(userId));
    }

    @Override
    public void cacheUserLikes(String userId, Set<String> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        srt.opsForSet().add(RedisKeyUtil.userLikes(userId), productIds.toArray(new String[0]));
    }

    @Override
    public void evictUserLikes(String userId) {
        srt.delete(RedisKeyUtil.userLikes(userId));
    }

    @Override
    public Long getProductLikesCount(String productId) {
        String v = srt.opsForValue().get(RedisKeyUtil.productLikes(productId));
        if (v == null) return null;
        try { return Long.parseLong(v); } catch (Exception e) { return null; }
    }

    @Override
    public void cacheProductLikesCount(String productId, long count) {
        srt.opsForValue().set(RedisKeyUtil.productLikes(productId), String.valueOf(count));
    }

    @Override
    public List<Long> getProductLikesCounts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) return List.of();

        List<String> keys = productIds.stream()
                .map(RedisKeyUtil::productLikes)
                .collect(Collectors.toList());

        List<String> vals = srt.opsForValue().multiGet(keys);
        if (vals == null) return Collections.nCopies(productIds.size(), null);

        List<Long> out = new ArrayList<>(vals.size());
        for (String v : vals) {
            if (v == null) out.add(null);
            else {
                try { out.add(Long.parseLong(v)); } catch (Exception e) { out.add(null); }
            }
        }
        return out;
    }
}
