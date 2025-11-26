package com.assignment.smartloading.service;

import com.assignment.smartloading.decision.*;
import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.repository.ProductLikeRepository;
import com.assignment.smartloading.repository.ProductRepository;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UnifiedRecommendationService {

    @Autowired private ProductRepository productRepo;
    @Autowired private ProductLikeRepository likeRepo;
    @Autowired private UserBehaviorRepository behaviorRepo;

    private final DecisionTreeEngine engine = new DecisionTreeEngine();
    private final DecisionNode tree = DecisionTreeBuilder.buildTree(engine);

    public DecisionTreeResult runDecisionTree(String userId) {

        RecommendationContext ctx = new RecommendationContext(userId);

        // ✅ Stage 0: Load ALL products (45)
        ctx.allProducts = productRepo.findAll();

        // ✅ User liked categories (sorted by most likes)
        ctx.likedCategories = likeRepo.findUserMostLikedCategories(userId);

        // ✅ User clicked categories (sorted by most clicks)
        ctx.clickedCategories = behaviorRepo.findUserMostClickedCategories(userId);

        // ✅ Global categories (liked + clicked)
        List<String> globalLiked = likeRepo.findGlobalTopLikedCategories();
        List<String> globalClicked = behaviorRepo.findGlobalTopClickedCategories();

        ctx.globalCategories = new ArrayList<>();
        ctx.globalCategories.addAll(globalLiked);
        ctx.globalCategories.addAll(globalClicked);

        // ✅ Run sequential tree (8 + 4 + 3 caps)
        tree.evaluate(ctx);

        // ✅ Category distribution inside FINAL 15
        Map<String, Long> categoryCount =
                ctx.final15Products.stream()
                        .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        // ✅ Top 3 categories in priority order (by where they were filled)
        LinkedHashSet<String> top3 = new LinkedHashSet<>();
        ctx.stage1LikesAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage2ClicksAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage3GlobalAdded.forEach(p -> top3.add(p.getCategory()));
        List<String> top3Categories = top3.stream().limit(3).toList();

        return new DecisionTreeResult(
                ctx.allProducts,          // initial 45
                ctx.stage1LikesAdded,     // stage1 (max 8)
                ctx.stage2ClicksAdded,    // stage2 (max 4)
                ctx.stage3GlobalAdded,    // stage3 (max 3)
                ctx.final15Products,      // final 15
                categoryCount,
                top3Categories,
                ctx.steps
        );
    }
}
