package com.assignment.smartloading.controller;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.dto.ProductDTO;
import com.assignment.smartloading.service.LikeService;
import com.assignment.smartloading.service.UnifiedRecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UnifiedRecommendationService recommendationService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            userId = "092ab9e0-6396-433c-b4ff-aae5c3025a0c";
        }

//        if (userId == null) {
//            return "redirect:/login";
//        }

        // run decision tree
        DecisionTreeResult result = recommendationService.runDecisionTree(userId);

        List<ProductDTO> finalProducts = Optional.ofNullable(result.getFinalProducts15()).orElse(Collections.emptyList());
        model.addAttribute("finalProducts", finalProducts);

        model.addAttribute("top3Categories", Optional.ofNullable(result.getTop3Categories()).orElse(Collections.emptyList()));
        model.addAttribute("categoryCount", Optional.ofNullable(result.getCategoryCount()).orElse(Collections.emptyMap()));

        //prepare product ids for like summary
        List<String> productIds = finalProducts.stream()
                .map(ProductDTO::getProductId)
                .collect(Collectors.toList());

        //fetch like info (go to redis checking first, if no data then go to postgreSQL)
        Map<String, Map<String, Object>> likeSummary = likeService.fetchLikeSummaryForProducts(userId, productIds);

        Map<String, Boolean> likedByUser = new HashMap<>();
        Map<String, String> likeText = new HashMap<>();

        for (ProductDTO p : finalProducts) {
            String pid = p.getProductId();
            Map<String, Object> info = likeSummary.get(pid);

            boolean liked = false;
            long totalLikes = 0L;

            if (info != null) {
                Object likedObj = info.get("liked");
                if (likedObj instanceof Boolean) {
                    liked = (Boolean) likedObj;
                } else if (likedObj instanceof String) {
                    liked = Boolean.parseBoolean((String) likedObj);
                }

                Object totalObj = info.get("totalLikes");
                if (totalObj instanceof Number) {
                    totalLikes = ((Number) totalObj).longValue();
                } else if (totalObj instanceof String) {
                    try {
                        totalLikes = Long.parseLong((String) totalObj);
                    } catch (NumberFormatException ignored) {
                        totalLikes = 0L;
                    }
                }
            }

            likedByUser.put(pid, liked);
            likeText.put(pid, totalLikes == 1 ? "1 like" : (totalLikes + " likes"));
        }

        model.addAttribute("userLikes", likedByUser);
        model.addAttribute("likeText", likeText);

        return "dashboard";
    }
}
