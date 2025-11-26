package com.assignment.smartloading.controller;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.service.LikeService;
import com.assignment.smartloading.service.UnifiedRecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UnifiedRecommendationService recommendationService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Run Sequential Decision Tree Pipeline
        DecisionTreeResult result = recommendationService.runDecisionTree(userId);

        // Final 15 products only
        List<Product> finalProducts = result.getFinalProducts15();
        model.addAttribute("finalProducts", finalProducts);

        // Top 3 categories in decision-tree priority order
        model.addAttribute("top3Categories", result.getTop3Categories());

        // Category distribution over final 15 (still used for breakdown UI)
        model.addAttribute("categoryCount", result.getCategoryCount());

        // Like/unlike status + like count text
        Map<String, Boolean> likedByUser = new HashMap<>();
        Map<String, String> likeText = new HashMap<>();

        for (Product p : finalProducts) {
            boolean liked = likeService.isLiked(userId, p.getProductId());
            long count = likeService.getLikes(p.getProductId());

            likedByUser.put(p.getProductId(), liked);
            likeText.put(p.getProductId(),
                    (count == 1 ? "1 like" : count + " likes"));
        }

        model.addAttribute("userLikes", likedByUser);
        model.addAttribute("likeText", likeText);

        return "dashboard";
    }
}
