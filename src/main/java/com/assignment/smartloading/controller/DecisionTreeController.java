package com.assignment.smartloading.controller;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.dto.ProductDTO;
import com.assignment.smartloading.service.UnifiedRecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class DecisionTreeController {

    @Autowired
    private UnifiedRecommendationService recommendationService;

    @GetMapping("/decision-tree")
    public String showDecisionTree(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        DecisionTreeResult result = recommendationService.runDecisionTree(userId);

        //use DTO lists (maybe empty if redis only store finalProducts)
        List<ProductDTO> initial = Optional.ofNullable(result.getInitialProducts()).orElse(Collections.emptyList());
        List<ProductDTO> s1 = Optional.ofNullable(result.getStage1Liked()).orElse(Collections.emptyList());
        List<ProductDTO> s2 = Optional.ofNullable(result.getStage2Clicks()).orElse(Collections.emptyList());
        List<ProductDTO> s3 = Optional.ofNullable(result.getStage3Global()).orElse(Collections.emptyList());
        List<ProductDTO> finalProducts = Optional.ofNullable(result.getFinalProducts15()).orElse(Collections.emptyList());
        List<String> steps = Optional.ofNullable(result.getSteps()).orElse(Collections.emptyList());

        model.addAttribute("initialProducts", initial);
        model.addAttribute("stage1Liked", s1);
        model.addAttribute("stage2Clicks", s2);
        model.addAttribute("stage3Global", s3);
        model.addAttribute("finalProducts", finalProducts);
        model.addAttribute("steps", steps);

        return "decision-tree";
    }
}
