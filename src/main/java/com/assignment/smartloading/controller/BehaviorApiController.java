package com.assignment.smartloading.controller;

import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.service.BehaviorService;
import com.assignment.smartloading.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/behavior")
public class BehaviorApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/click")
    public ResponseEntity<String> recordClick(
            @RequestParam("productId") String productId,
            HttpSession session) {

        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("USER_NOT_LOGGED_IN");
        }

        Product p = productService.getProductById(productId);

        if (p == null) {
            return ResponseEntity.status(404).body("PRODUCT_NOT_FOUND");
        }

        behaviorService.addClick(userId, productId, p.getCategory());

        return ResponseEntity.ok("OK");
    }
}
