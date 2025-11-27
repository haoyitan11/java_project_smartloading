package com.assignment.smartloading.dto;

import com.assignment.smartloading.model.Product;
import java.util.List;
import java.util.Map;

public class DecisionTreeResult {

    private final List<Product> initialProducts; // 45 product total
    private final List<Product> stage1Liked;     // 8 products max
    private final List<Product> stage2Clicks;    // 4 products max
    private final List<Product> stage3Global;    // fulfill remaining
    private final List<Product> finalProducts15; // first 15 product displayed

    private final Map<String, Long> categoryCount;
    private final List<String> top3Categories;
    private final List<String> steps;

    public DecisionTreeResult(
            List<Product> initial,
            List<Product> s1,
            List<Product> s2,
            List<Product> s3,
            List<Product> final15,
            Map<String, Long> categoryCount,
            List<String> top3Categories,
            List<String> steps
    ) {
        this.initialProducts = initial;
        this.stage1Liked = s1;
        this.stage2Clicks = s2;
        this.stage3Global = s3;
        this.finalProducts15 = final15;
        this.categoryCount = categoryCount;
        this.top3Categories = top3Categories;
        this.steps = steps;
    }

    public List<Product> getInitialProducts() { return initialProducts; }
    public List<Product> getStage1Liked() { return stage1Liked; }
    public List<Product> getStage2Clicks() { return stage2Clicks; }
    public List<Product> getStage3Global() { return stage3Global; }

    public List<Product> getFinalProducts15() { return finalProducts15; }
    public Map<String, Long> getCategoryCount() { return categoryCount; }
    public List<String> getTop3Categories() { return top3Categories; }
    public List<String> getSteps() { return steps; }
}
