package com.assignment.smartloading.controller;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.service.UnifiedRecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DecisionTreeController {

    @Autowired
    private UnifiedRecommendationService recommendationService;

    @GetMapping("/decision-tree")
    public String showDecisionTree(Model model, HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Correct method name
        DecisionTreeResult result = recommendationService.runDecisionTree(userId);

        model.addAttribute("initialProducts", result.getInitialProducts());
        model.addAttribute("stage1Liked", result.getStage1Liked());
        model.addAttribute("stage2Clicks", result.getStage2Clicks());
        model.addAttribute("stage3GlobalLiked", result.getStage3GlobalLiked());
        model.addAttribute("stage4GlobalClicks", result.getStage4GlobalClicks());
        model.addAttribute("finalProducts", result.getFinalProducts());
        model.addAttribute("steps", result.getSteps());

        return "decision-tree";
    }
}
