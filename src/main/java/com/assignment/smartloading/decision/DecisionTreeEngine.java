package com.assignment.smartloading.decision;

import com.assignment.smartloading.model.Product;

import java.util.List;

public class DecisionTreeEngine {

    // Rule 1: add up to 8 from liked categories
    public void applyLikes(RecommendationContext ctx) {
        fillFromCategories(ctx, ctx.likedCategories, 8, ctx.stage1LikesAdded);
    }

    // Rule 2: add up to 4 from clicked categories
    public void applyClicks(RecommendationContext ctx) {
        fillFromCategories(ctx, ctx.clickedCategories, 4, ctx.stage2ClicksAdded);
    }

    // Rule 3: add up to 3 from global categories
    public void applyGlobal(RecommendationContext ctx) {
        fillFromCategories(ctx, ctx.globalCategories, 3, ctx.stage3GlobalAdded);
    }

    private void fillFromCategories(RecommendationContext ctx,
                                    List<String> categories,
                                    int maxToAdd,
                                    List<Product> stageList) {

        if (categories == null || categories.isEmpty()) return;

        int added = 0;

        for (String cat : categories) {
            for (Product p : ctx.allProducts) {
                if (!ctx.needMore15()) return;
                if (added >= maxToAdd) return;

                if (cat.equalsIgnoreCase(p.getCategory()) && !ctx.alreadyChosen(p)) {
                    ctx.addProduct(p, stageList);
                    added++;
                }
            }
        }
    }
}
