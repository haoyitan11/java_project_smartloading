package com.assignment.smartloading.decision;

import com.assignment.smartloading.model.Product;

import java.util.*;

public class RecommendationContext {

    public final String userId;

    // inputs
    public List<String> likedCategories = new ArrayList<>();
    public List<String> clickedCategories = new ArrayList<>();
    public List<String> globalCategories = new ArrayList<>();
    public List<Product> allProducts = new ArrayList<>();

    // outputs
    public final List<Product> final15Products = new ArrayList<>();

    // stages for visualization/debug
    public final List<Product> stage1LikesAdded = new ArrayList<>();
    public final List<Product> stage2ClicksAdded = new ArrayList<>();
    public final List<Product> stage3GlobalAdded = new ArrayList<>();

    public final List<String> steps = new ArrayList<>();

    // to prevent duplicates
    public final Set<String> chosenProductIds = new HashSet<>();

    public RecommendationContext(String userId) {
        this.userId = userId;
    }

    public boolean needMore15() {
        return final15Products.size() < 15;
    }

    public int remaining() {
        return 15 - final15Products.size();
    }

    public boolean alreadyChosen(Product p) {
        return chosenProductIds.contains(p.getProductId());
    }

    public void addProduct(Product p, List<Product> stageList) {
        if (p == null) return;
        if (alreadyChosen(p)) return;
        if (!needMore15()) return;

        chosenProductIds.add(p.getProductId());
        final15Products.add(p);
        stageList.add(p);
    }
}
