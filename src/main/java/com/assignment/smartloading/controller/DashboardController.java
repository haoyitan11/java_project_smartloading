package com.assignment.smartloading.controller;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.service.LikeService;
import com.assignment.smartloading.service.ProductService;
import com.assignment.smartloading.service.UnifiedRecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class DashboardController {

    @Autowired private ProductService productService;
    @Autowired private LikeService likeService;
    @Autowired private UnifiedRecommendationService recommendationService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Run Unified Decision Pipeline
        DecisionTreeResult result = recommendationService.runDecisionTree(userId);

        // Final Top 5 Products
        List<Product> finalProducts = result.getFinalProducts();
        model.addAttribute("finalProducts", finalProducts);

        // Compute final categories from final products
        List<String> finalCategories = finalProducts.stream()
                .map(Product::getCategory)
                .distinct()
                .toList();

        model.addAttribute("finalCategories", finalCategories);

        // UI Badge Recommendation
        model.addAttribute("recommendedCategory",
                finalCategories.isEmpty() ? "None" : finalCategories.get(0));

        // Steps for Debugging/Explanation
        model.addAttribute("steps", result.getSteps());

        // Like/Unlike Status
        Map<String, Boolean> likedByUser = new HashMap<>();
        Map<String, String> likeText = new HashMap<>();

        for (Product p : finalProducts) {
            boolean liked = likeService.isLiked(userId, p.getProductId());
            long count = likeService.getLikes(p.getProductId());

            likedByUser.put(p.getProductId(), liked);
            likeText.put(
                    p.getProductId(),
                    (count == 1 ? "1 like" : count + " likes")
            );
        }

        model.addAttribute("userLikes", likedByUser);
        model.addAttribute("likeText", likeText);

        return "dashboard";
    }
}
