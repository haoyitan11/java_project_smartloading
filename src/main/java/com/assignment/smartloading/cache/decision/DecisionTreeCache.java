package com.assignment.smartloading.cache.decision;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.dto.ProductDTO;

import java.util.List;
import java.util.Map;

public interface DecisionTreeCache {
    void saveDecision(String userId,
                      List<ProductDTO> finalProducts,
                      Map<String, Long> categoryCount,
                      List<String> top3Categories,
                      DecisionTreeResult debug);

    void evictDecision(String userId);
}
