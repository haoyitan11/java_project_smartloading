package com.assignment.smartloading.cache.decision;

import com.assignment.smartloading.cache.RedisKeyUtil;
import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@Profile("k8s")
public class RedisDecisionTreeCache implements DecisionTreeCache {

    private static final Logger log = LoggerFactory.getLogger(RedisDecisionTreeCache.class);

    private final StringRedisTemplate srt;
    private final ObjectMapper objectMapper;

    private static final Duration DECISION_TTL = Duration.ofMinutes(10);

    public RedisDecisionTreeCache(StringRedisTemplate srt, ObjectMapper objectMapper) {
        this.srt = srt;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveDecision(String userId,
                             List<ProductDTO> finalProducts,
                             Map<String, Long> categoryCount,
                             List<String> top3Categories,
                             DecisionTreeResult debug) {
        try {
            String finalKey = RedisKeyUtil.decisionTreeFinal(userId);
            String metaKey = RedisKeyUtil.decisionTreeMeta(userId);
            String debugKey = RedisKeyUtil.decisionTreeDebug(userId);

            List<String> finalIds = finalProducts.stream().map(ProductDTO::getProductId).toList();
            srt.opsForValue().set(finalKey, objectMapper.writeValueAsString(finalIds), DECISION_TTL);

            Map<String, Object> meta = new HashMap<>();
            meta.put("categoryCount", categoryCount);
            meta.put("top3Categories", top3Categories);
            srt.opsForValue().set(metaKey, objectMapper.writeValueAsString(meta), DECISION_TTL);

            srt.opsForValue().set(debugKey,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(debug),
                    DECISION_TTL);

            for (ProductDTO dto : finalProducts) {
                srt.opsForValue().set(RedisKeyUtil.productJson(dto.getProductId()),
                        objectMapper.writeValueAsString(dto));
            }
        } catch (Exception e) {
            log.warn("Failed to write decision caches", e);
        }
    }

    @Override
    public void evictDecision(String userId) {
        try {
            srt.delete(RedisKeyUtil.decisionTreeFinal(userId));
            srt.delete(RedisKeyUtil.decisionTreeMeta(userId));
            srt.delete(RedisKeyUtil.decisionTreeDebug(userId));
        } catch (Exception e) {
            log.warn("Failed to evict decision caches userId={}", userId, e);
        }
    }
}
