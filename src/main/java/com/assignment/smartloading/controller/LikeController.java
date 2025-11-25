package com.assignment.smartloading.controller;

import com.assignment.smartloading.service.LikeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/toggle")
    public Map<String, Object> toggleLike(@RequestParam String productId,
                                          HttpSession session) {

        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            return Map.of(
                    "error", true,
                    "message", "USER_NOT_LOGGED_IN"
            );
        }

        boolean liked = likeService.toggleLike(userId, productId);
        long totalLikes = likeService.getLikes(productId);

        return Map.of(
                "error", false,
                "liked", liked,
                "totalLikes", totalLikes
        );
    }
}
