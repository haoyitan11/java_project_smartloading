package com.assignment.smartloading.service;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.repository.ProductLikeRepository;
import com.assignment.smartloading.repository.ProductRepository;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnifiedRecommendationService {

    @Autowired private ProductRepository productRepo;
    @Autowired private ProductLikeRepository likeRepo;
    @Autowired private UserBehaviorRepository behaviorRepo;

    private List<Product> filter(List<Product> source, List<String> categories) {
        if (categories == null || categories.isEmpty()) return new ArrayList<>();
        List<Product> out = new ArrayList<>();
        for (Product p : source) {
            if (categories.contains(p.getCategory()))
                out.add(p);
        }
        return out;
    }

    private List<Product> fallback(List<Product> list, List<Product> original) {
        return (list == null || list.isEmpty()) ? new ArrayList<>(original) : list;
    }

    public DecisionTreeResult runDecisionTree(String userId) {

        List<String> steps = new ArrayList<>();

        // 1) Load 15 products
        List<Product> initial = productRepo.findTop15();
        steps.add("Loaded initial 15 products.");

        // 2) Personal Likes
        List<String> likedCats = likeRepo.findUserMostLikedCategories(userId);
        List<Product> stage1 = filter(initial, likedCats);
        steps.add("Filtered by personal likes → " + stage1.size());

        // 3) Personal Clicks
        List<String> clickCats = behaviorRepo.findUserMostClickedCategories(userId);
        List<Product> stage2 = filter(fallback(stage1, initial), clickCats);
        steps.add("Filtered by personal clicks → " + stage2.size());

        // 4) Global Likes
        List<String> globalLiked = likeRepo.findGlobalTopLikedCategories();
        List<Product> stage3 = filter(fallback(stage2, initial), globalLiked);
        steps.add("Filtered by global likes → " + stage3.size());

        // 5) Global Clicks
        List<String> globalClicked = behaviorRepo.findGlobalTopClickedCategories();
        List<Product> stage4 = filter(fallback(stage3, initial), globalClicked);
        steps.add("Filtered by global clicks → " + stage4.size());

        // 6) Final Top 5
        List<Product> finalProducts = stage4.stream().limit(5).toList();
        steps.add("Selected final top 5 products.");

        return new DecisionTreeResult(
                initial,
                stage1,
                stage2,
                stage3,
                stage4,
                finalProducts,
                steps
        );
    }
}