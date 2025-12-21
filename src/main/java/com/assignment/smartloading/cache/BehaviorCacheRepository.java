package com.assignment.smartloading.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BehaviorCacheRepository {

    private final StringRedisTemplate redis;

    private static final Duration USER_CLICK_TTL = Duration.ofHours(6);

    public BehaviorCacheRepository(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void recordClick(String userId, String category) {
        String hashKey = RedisKeyUtil.userClicks(userId);
        String zsetKey = RedisKeyUtil.userCategoryZset(userId);

        redis.opsForHash().increment(hashKey, category, 1);
        redis.expire(hashKey, USER_CLICK_TTL);

        redis.opsForZSet().incrementScore(zsetKey, category, 1.0);
        redis.expire(zsetKey, USER_CLICK_TTL);

        // global can be long-lived
        redis.opsForZSet().incrementScore(RedisKeyUtil.globalCategoryZset(), category, 1.0);
    }
}
