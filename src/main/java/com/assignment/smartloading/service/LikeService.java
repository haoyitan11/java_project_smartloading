package com.assignment.smartloading.service;

import com.assignment.smartloading.logging.JsonLoggerService;
import com.assignment.smartloading.model.ProductLike;
import com.assignment.smartloading.repository.ProductLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {

    @Autowired
    private ProductLikeRepository repo;

    @Autowired
    private JsonLoggerService logger;

    public boolean toggleLike(String userId, String productId) {

        var existing = repo.findByUserIdAndProductId(userId, productId);

        boolean nowLiked;

        if (existing.isPresent()) {
            repo.delete(existing.get());
            nowLiked = false;
        } else {
            repo.save(new ProductLike(userId, productId));
            nowLiked = true;
        }

        logger.logLike(userId, productId, nowLiked);

        return nowLiked;
    }

    public long getLikes(String productId) {
        return repo.countByProductId(productId);
    }

    public boolean isLiked(String userId, String productId) {
        return repo.existsByUserIdAndProductId(userId, productId);
    }

    public List<String> getTopLikedCategories(String userId) {
        return repo.findUserMostLikedCategories(userId);
    }
}