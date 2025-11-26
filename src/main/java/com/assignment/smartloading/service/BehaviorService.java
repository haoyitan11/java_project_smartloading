package com.assignment.smartloading.service;

import com.assignment.smartloading.logging.JsonLoggerService;
import com.assignment.smartloading.model.UserBehavior;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BehaviorService {

    @Autowired private UserBehaviorRepository repo;
    @Autowired private JsonLoggerService logger;

    public void addClick(String userId, String productId, String category) {

        logger.logClick(userId, productId, category);

        UserBehavior existing = repo.findByUserIdAndCategory(userId, category);

        if (existing == null) {
            repo.save(new UserBehavior(userId, category, 1));
        } else {
            existing.setClicks(existing.getClicks() + 1);
            existing.setLastUpdated(new Date());
            repo.save(existing);
        }
    }

    public List<UserBehavior> getUserBehavior(String userId) {
        return repo.findAllByUserId(userId);
    }
}
