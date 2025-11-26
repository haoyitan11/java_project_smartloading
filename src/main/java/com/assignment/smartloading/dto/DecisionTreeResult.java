package com.assignment.smartloading.dto;

import com.assignment.smartloading.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class DecisionTreeResult {

    private List<Product> initialProducts;
    private List<Product> stage1Liked;
    private List<Product> stage2Clicks;
    private List<Product> stage3GlobalLiked;
    private List<Product> stage4GlobalClicks;
    private List<Product> finalProducts;
    private List<String> steps;

    public DecisionTreeResult(
            List<Product> initial,
            List<Product> s1,
            List<Product> s2,
            List<Product> s3,
            List<Product> s4,
            List<Product> finalTop5,
            List<String> steps
    ) {
        this.initialProducts = initial;
        this.stage1Liked = s1;
        this.stage2Clicks = s2;
        this.stage3GlobalLiked = s3;
        this.stage4GlobalClicks = s4;
        this.finalProducts = finalTop5;
        this.steps = steps;
    }

    // GETTERS
    public List<Product> getInitialProducts() { return initialProducts; }
    public List<Product> getStage1Liked() { return stage1Liked; }
    public List<Product> getStage2Clicks() { return stage2Clicks; }
    public List<Product> getStage3GlobalLiked() { return stage3GlobalLiked; }
    public List<Product> getStage4GlobalClicks() { return stage4GlobalClicks; }
    public List<Product> getFinalProducts() { return finalProducts; }
    public List<String> getSteps() { return steps; }

    // Convert final products → categories
    public List<String> getFinalCategories() {
        return finalProducts.stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
}
