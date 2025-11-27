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

        // Stage 0: load all products
        ctx.allProducts = productRepo.findAll();

        //rule 1 input
        ctx.likedCategories = likeRepo.findUserMostLikedCategories(userId);

        //rule 2 input
        ctx.clickedCategories = behaviorRepo.findUserMostClickedCategories(userId);

        //rule 3 input (entire user liked + clicked)
        LinkedHashSet<String> globals = new LinkedHashSet<>();
        globals.addAll(likeRepo.findGlobalTopLikedCategories());
        globals.addAll(behaviorRepo.findGlobalTopClickedCategories());
        ctx.globalCategories = new ArrayList<>(globals);

        //run sequential pipeline (8 + 4 + remaining)
        tree.evaluate(ctx);

        //category distribution after final stage
        Map<String, Long> categoryCount =
                ctx.final15Products.stream()
                        .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        //top 3 categories in decision-tree priority order
        LinkedHashSet<String> top3 = new LinkedHashSet<>();
        ctx.stage1LikesAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage2ClicksAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage3GlobalAdded.forEach(p -> top3.add(p.getCategory()));
        List<String> top3Categories = top3.stream().limit(3).toList();

        return new DecisionTreeResult(
                ctx.allProducts,
                ctx.stage1LikesAdded,
                ctx.stage2ClicksAdded,
                ctx.stage3GlobalAdded,
                ctx.final15Products,
                categoryCount,
                top3Categories,
                ctx.steps
        );
    }
}
