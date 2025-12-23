package com.assignment.smartloading.cache.decision;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.dto.ProductDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Profile("local")
public class NoRedisDecisionTreeCache implements DecisionTreeCache {
    @Override
    public void saveDecision(String userId, List<ProductDTO> finalProducts, Map<String, Long> categoryCount,
                             List<String> top3Categories, DecisionTreeResult debug) {
        // no-op
    }

    @Override
    public void evictDecision(String userId) {
        // no-op
    }
}
