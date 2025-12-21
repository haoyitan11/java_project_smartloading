package com.assignment.smartloading.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DecisionTreeResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<ProductDTO> initialProducts;
    private final List<ProductDTO> stage1Liked;
    private final List<ProductDTO> stage2Clicks;
    private final List<ProductDTO> stage3Global;
    private final List<ProductDTO> finalProducts15;
    private final Map<String, Long> categoryCount;
    private final List<String> top3Categories;
    private final List<String> steps;

    @JsonCreator
    public DecisionTreeResult(
            @JsonProperty("initialProducts") List<ProductDTO> initialProducts,
            @JsonProperty("stage1Liked") List<ProductDTO> stage1Liked,
            @JsonProperty("stage2Clicks") List<ProductDTO> stage2Clicks,
            @JsonProperty("stage3Global") List<ProductDTO> stage3Global,
            @JsonProperty("finalProducts15") List<ProductDTO> finalProducts15,
            @JsonProperty("categoryCount") Map<String, Long> categoryCount,
            @JsonProperty("top3Categories") List<String> top3Categories,
            @JsonProperty("steps") List<String> steps
    ) {
        this.initialProducts = initialProducts;
        this.stage1Liked = stage1Liked;
        this.stage2Clicks = stage2Clicks;
        this.stage3Global = stage3Global;
        this.finalProducts15 = finalProducts15;
        this.categoryCount = categoryCount;
        this.top3Categories = top3Categories;
        this.steps = steps;
    }

    // getters
    public List<ProductDTO> getInitialProducts() { return initialProducts; }
    public List<ProductDTO> getStage1Liked() { return stage1Liked; }
    public List<ProductDTO> getStage2Clicks() { return stage2Clicks; }
    public List<ProductDTO> getStage3Global() { return stage3Global; }
    public List<ProductDTO> getFinalProducts15() { return finalProducts15; }
    public Map<String, Long> getCategoryCount() { return categoryCount; }
    public List<String> getTop3Categories() { return top3Categories; }
    public List<String> getSteps() { return steps; }

    @Override
    public String toString() {
        return "DecisionTreeResult{final=" + (finalProducts15 != null ? finalProducts15.size() : 0) + "}";
    }
}
