package com.assignment.smartloading.service;

import com.assignment.smartloading.logging.JsonLoggerService;
import com.assignment.smartloading.model.UserBehavior;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BehaviorService {

    @Autowired private UserBehaviorRepository repo;
    @Autowired private JsonLoggerService logger;

    public void addClick(String userId, String productId, String category) {

        // log click event in JSON file
        logger.logClick(userId, productId, category);

        // update DB click counter per user+category
        UserBehavior existing = repo.findByUserIdAndCategory(userId, category);

        if (existing == null) {
            repo.save(new UserBehavior(userId, category, 1));
        } else {
            existing.setClicks(existing.getClicks() + 1);
            repo.save(existing);
            // lastUpdated auto-handled by @PreUpdate
        }
    }
}
