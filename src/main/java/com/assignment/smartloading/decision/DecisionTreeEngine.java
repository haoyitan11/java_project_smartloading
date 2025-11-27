package com.assignment.smartloading.decision;

import com.assignment.smartloading.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DecisionTreeEngine {

    //Rule 1: max 8 product from personal like
    public void applyLikes(RecommendationContext ctx) {
        fillFromCategories(ctx, ctx.likedCategories, 8, ctx.stage1LikesAdded,
                "Rule 1: Personal Likes");
    }

    //Rule 2: max 4 product from personal clicks
    public void applyClicks(RecommendationContext ctx) {
        fillFromCategories(ctx, ctx.clickedCategories, 4, ctx.stage2ClicksAdded,
                "Rule 2: Personal Clicks");
    }

    //Rule 3: fulfilling remaining to reach 15 products display (entire user click + like)
    public void applyGlobal(RecommendationContext ctx) {
        int remaining = ctx.remainingSlots();
        fillFromCategories(ctx, ctx.globalCategories, remaining, ctx.stage3GlobalAdded,
                "Rule 3: Global Fallback");
    }

    //avoid duplicated, check products, checks category priority order
    private void fillFromCategories(
            RecommendationContext ctx,
            List<String> categories,
            int cap,
            List<Product> stageOut,
            String stepName
    ) {
        if (!ctx.needMore()) return;
        if (categories == null || categories.isEmpty()) {
            ctx.steps.add(stepName + " → skipped (no categories)");
            return;
        }

        int added = 0;
        List<Product> pool = new ArrayList<>(ctx.allProducts);

        //category priority order from database
        for (String cat : categories) {
            if (!ctx.needMore() || added >= cap) break;

            for (Product p : pool) {
                if (!ctx.needMore() || added >= cap) break;

                if (cat.equalsIgnoreCase(p.getCategory())) {
                    boolean ok = ctx.addProduct(p);
                    if (ok) {
                        stageOut.add(p);
                        added++;
                    }
                }
            }
        }

        ctx.steps.add(stepName + " → added " + added);
    }
}
