package com.assignment.smartloading.controller;

import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.service.BehaviorService;
import com.assignment.smartloading.service.LikeService;
import com.assignment.smartloading.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class DashboardController {

    @Autowired
    private ProductService productService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private BehaviorService behaviorService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        /* ================================
           RECOMMENDED CATEGORY LABEL
        ================================ */
        String recommended = behaviorService.getMostViewedCategory(userId);
        model.addAttribute("recommendedCategory",
                recommended != null ? recommended : "popular");

        /* ================================
           LOAD TOP 5 CATEGORIES
        ================================ */
        List<String> categories = productService.getAllCategories();
        if (categories == null) categories = new ArrayList<>();

        Map<String, List<Product>> categoryProductMap = new LinkedHashMap<>();
        Map<String, Boolean> userLikes = new HashMap<>();
        Map<String, String> likeText = new HashMap<>();

        categories.stream()
                .filter(Objects::nonNull)
                .limit(5)
                .forEach(cat -> {

                    List<Product> products =
                            Optional.ofNullable(productService.getProductsByCategory(cat))
                                    .orElse(Collections.emptyList());

                    categoryProductMap.put(cat, products);

                    for (Product p : products) {

                        String pid = p.getProductId();

                        boolean liked = likeService.isLiked(userId, pid);
                        long count = likeService.getLikes(pid);

                        userLikes.put(pid, liked);

                        String txt = (count == 0)
                                ? "0 likes"
                                : (count == 1 ? "1 like" : count + " likes");

                        likeText.put(pid, txt);
                    }
                });

        model.addAttribute("categoryProductMap", categoryProductMap);
        model.addAttribute("userLikes", userLikes);
        model.addAttribute("likeText", likeText);

        return "dashboard";
    }
}
