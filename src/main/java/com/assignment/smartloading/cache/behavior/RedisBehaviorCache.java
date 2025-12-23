package com.assignment.smartloading.cache.behavior;

import com.assignment.smartloading.cache.RedisKeyUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("k8s")
public class RedisBehaviorCache implements BehaviorCache {

    private final StringRedisTemplate redis;
    private static final Duration USER_CLICK_TTL = Duration.ofHours(6);

    public RedisBehaviorCache(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void recordClick(String userId, String category) {
        String hashKey = RedisKeyUtil.userClicks(userId);
        String zsetKey = RedisKeyUtil.userCategoryZset(userId);

        redis.opsForHash().increment(hashKey, category, 1);
        redis.expire(hashKey, USER_CLICK_TTL);

        redis.opsForZSet().incrementScore(zsetKey, category, 1.0);
        redis.expire(zsetKey, USER_CLICK_TTL);

        redis.opsForZSet().incrementScore(RedisKeyUtil.globalCategoryZset(), category, 1.0);
    }
}
