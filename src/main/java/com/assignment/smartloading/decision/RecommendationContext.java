package com.assignment.smartloading.decision;

import com.assignment.smartloading.model.Product;

import java.util.*;

public class RecommendationContext {

    public final String userId;

    //inputs
    public List<Product> allProducts = new ArrayList<>();
    public List<String> likedCategories = new ArrayList<>();
    public List<String> clickedCategories = new ArrayList<>();
    public List<String> globalCategories = new ArrayList<>();

    //outputs product by stage
    public final List<Product> stage1LikesAdded = new ArrayList<>();
    public final List<Product> stage2ClicksAdded = new ArrayList<>();
    public final List<Product> stage3GlobalAdded = new ArrayList<>();

    public final List<Product> final15Products = new ArrayList<>();
    public final List<String> steps = new ArrayList<>();

   //tracking duplication
    public final Set<String> chosenProductIds = new HashSet<>();

    public RecommendationContext(String userId) {
        this.userId = userId;
    }

    //checking stage 3 remaining product to fill
    public int remainingSlots() {
        return Math.max(0, 15 - final15Products.size());
    }

    //check pipeline whether to continue or not
    public boolean needMore() {
        return final15Products.size() < 15;
    }

    //avoid product repeat display twice
    public boolean addProduct(Product p) {
        if (p == null) return false;
        if (chosenProductIds.contains(p.getProductId())) return false;

        chosenProductIds.add(p.getProductId());
        final15Products.add(p);
        return true;
    }
}
