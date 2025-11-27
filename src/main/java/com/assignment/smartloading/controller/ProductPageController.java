package com.assignment.smartloading.controller;

import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.service.LikeService;
import com.assignment.smartloading.service.ProductService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class ProductPageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/products")
    public String showAllProducts(Model model, HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        //load all categories
        List<String> categories = productService.getAllCategories();
        Map<String, List<Map<String, Object>>> categoryMap = new LinkedHashMap<>();

        for (String c : categories) {

            List<Product> products = productService.getProductsByCategory(c);
            List<Map<String, Object>> listWithLikes = new ArrayList<>();

            for (Product p : products) {

                boolean liked = likeService.isLiked(userId, p.getProductId());
                long likes = likeService.getLikes(p.getProductId());

                Map<String, Object> map = new HashMap<>();
                map.put("productId", p.getProductId());
                map.put("productName", p.getProductName());
                map.put("price", p.getPrice());
                map.put("description", p.getDescription());
                map.put("imageUrl", p.getImageUrl());
                map.put("likedByUser", liked);
                map.put("likes", likes);

                listWithLikes.add(map);
            }

            categoryMap.put(c, listWithLikes);
        }

        model.addAttribute("categoryMap", categoryMap);
        return "product";
    }
}
