package com.assignment.smartloading.dto;

import com.assignment.smartloading.model.Product;
import java.util.List;
import java.util.Map;

public class RecommendationResult {

    private List<String> finalCategories;           // final 5 categories
    private List<Product> finalProducts;            // final top 5 products
    private List<String> steps;                     // decision tree steps

    private Map<String, List<Product>> productsByCategory; // dashboard view

    // For Decision Tree Visualization:
    private List<Product> initialProducts;          // 15 products
    private List<Product> stage1Liked;
    private List<Product> stage2Clicked;
    private List<Product> stage3GlobalLiked;
    private List<Product> stage4GlobalClicked;

    public RecommendationResult() {}

    public RecommendationResult(
            List<String> finalCategories,
            List<Product> finalProducts,
            List<String> steps,
            Map<String, List<Product>> productsByCategory,
            List<Product> initialProducts,
            List<Product> stage1Liked,
            List<Product> stage2Clicked,
            List<Product> stage3GlobalLiked,
            List<Product> stage4GlobalClicked
    ) {
        this.finalCategories = finalCategories;
        this.finalProducts = finalProducts;
        this.steps = steps;
        this.productsByCategory = productsByCategory;
        this.initialProducts = initialProducts;
        this.stage1Liked = stage1Liked;
        this.stage2Clicked = stage2Clicked;
        this.stage3GlobalLiked = stage3GlobalLiked;
        this.stage4GlobalClicked = stage4GlobalClicked;
    }

    // ================= GETTERS =================
    public List<String> getFinalCategories() { return finalCategories; }
    public List<Product> getFinalProducts() { return finalProducts; }
    public List<String> getSteps() { return steps; }

    public Map<String, List<Product>> getProductsByCategory() { return productsByCategory; }

    public List<Product> getInitialProducts() { return initialProducts; }
    public List<Product> getStage1Liked() { return stage1Liked; }
    public List<Product> getStage2Clicked() { return stage2Clicked; }
    public List<Product> getStage3GlobalLiked() { return stage3GlobalLiked; }
    public List<Product> getStage4GlobalClicked() { return stage4GlobalClicked; }
}
