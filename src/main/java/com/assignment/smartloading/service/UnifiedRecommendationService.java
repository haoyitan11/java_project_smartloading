package com.assignment.smartloading.service;

import com.assignment.smartloading.cache.decision.DecisionTreeCache;
import com.assignment.smartloading.decision.*;
import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.dto.ProductDTO;
import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.repository.ProductLikeRepository;
import com.assignment.smartloading.repository.ProductRepository;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UnifiedRecommendationService {

    private final ProductRepository productRepo;
    private final ProductLikeRepository likeRepo;
    private final UserBehaviorRepository behaviorRepo;
    private final DecisionTreeCache decisionTreeCache;

    private final DecisionTreeEngine engine = new DecisionTreeEngine();
    private final DecisionNode tree = DecisionTreeBuilder.buildTree(engine);

    public UnifiedRecommendationService(ProductRepository productRepo,
                                        ProductLikeRepository likeRepo,
                                        UserBehaviorRepository behaviorRepo,
                                        DecisionTreeCache decisionTreeCache) {
        this.productRepo = productRepo;
        this.likeRepo = likeRepo;
        this.behaviorRepo = behaviorRepo;
        this.decisionTreeCache = decisionTreeCache;
    }

    public DecisionTreeResult runDecisionTree(String userId) {

        RecommendationContext ctx = new RecommendationContext(userId);
        ctx.allProducts = productRepo.findAll();
        ctx.likedCategories = likeRepo.findUserMostLikedCategories(userId);
        ctx.clickedCategories = behaviorRepo.findUserMostClickedCategories(userId);

        LinkedHashSet<String> globals = new LinkedHashSet<>();
        globals.addAll(likeRepo.findGlobalTopLikedCategories());
        globals.addAll(behaviorRepo.findGlobalTopClickedCategories());
        ctx.globalCategories = new ArrayList<>(globals);

        tree.evaluate(ctx);

        Map<String, Long> categoryCount = ctx.final15Products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        LinkedHashSet<String> top3 = new LinkedHashSet<>();
        ctx.stage1LikesAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage2ClicksAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage3GlobalAdded.forEach(p -> top3.add(p.getCategory()));
        List<String> top3Categories = top3.stream().limit(3).toList();

        List<ProductDTO> initialDtos = ctx.allProducts.stream().map(ProductDTO::from).toList();
        List<ProductDTO> s1Dtos = ctx.stage1LikesAdded.stream().map(ProductDTO::from).toList();
        List<ProductDTO> s2Dtos = ctx.stage2ClicksAdded.stream().map(ProductDTO::from).toList();
        List<ProductDTO> s3Dtos = ctx.stage3GlobalAdded.stream().map(ProductDTO::from).toList();
        List<ProductDTO> finalDtos = ctx.final15Products.stream().map(ProductDTO::from).toList();
        List<String> steps = ctx.steps == null ? List.of() : List.copyOf(ctx.steps);

        DecisionTreeResult result = new DecisionTreeResult(
                initialDtos, s1Dtos, s2Dtos, s3Dtos, finalDtos,
                categoryCount, top3Categories, steps
        );

        // redis in k8s; without redis in local
        decisionTreeCache.saveDecision(userId, finalDtos, categoryCount, top3Categories, result);

        return result;
    }

    public void evictDecisionTreeForUser(String userId) {
        decisionTreeCache.evictDecision(userId);
    }
}
