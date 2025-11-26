package com.assignment.smartloading.service;

import com.assignment.smartloading.decision.*;
import com.assignment.smartloading.dto.RecommendationResult;
import com.assignment.smartloading.model.UserBehavior;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DecisionTreeService {

    private final DecisionTreeNode tree = DecisionTreeBuilder.buildTree();

    /**
     * Evaluates the decision tree and returns RecommendationResult
     * compatible with the 9-argument DTO constructor.
     */
    public RecommendationResult evaluate(DecisionContext ctx) {

        List<String> steps = new ArrayList<>();

        // Evaluate decision tree path
        DecisionOutcome outcome = tree.evaluate(ctx, steps);

        // Resolve final 5 categories
        List<String> finalCategories = resolve(outcome, ctx);

        // DecisionTreeService does NOT calculate products.
        // So we pass null for ALL product-related fields.

        return new RecommendationResult(
                finalCategories,   // final categories
                null,              // final products (UnifiedRecommendationService will handle)
                steps,             // steps
                null,              // productsByCategory
                null,              // initialProducts
                null,              // stage1Liked
                null,              // stage2Clicked
                null,              // stage3GlobalLiked
                null               // stage4GlobalClicked
        );
    }

    /**
     * Resolves the categories based on the outcome rule.
     */
    private List<String> resolve(DecisionOutcome out, DecisionContext ctx) {

        switch (out.getType()) {

            case LIKES:
                return ctx.likedCategories.stream()
                        .distinct()
                        .limit(5)
                        .toList();

            case CLICKS:
                return ctx.behavior.stream()
                        .sorted((a, b) -> b.getClicks() - a.getClicks())
                        .map(UserBehavior::getCategory)
                        .distinct()
                        .limit(5)
                        .toList();

            default:
                return out.getCategories().stream()
                        .limit(5)
                        .toList();
        }
    }
}
