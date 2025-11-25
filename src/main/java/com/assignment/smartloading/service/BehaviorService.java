package com.assignment.smartloading.service;

import com.assignment.smartloading.logging.JsonLoggerService;
import com.assignment.smartloading.model.UserBehavior;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BehaviorService {

    @Autowired
    private UserBehaviorRepository repo;

    @Autowired
    private JsonLoggerService logger;

    /* ======================================================
       RECORD USER CLICK + UPDATE BEHAVIOR TABLE
    ====================================================== */
    public void addClick(String userId, String productId, String category) {

        logger.logClick(userId, productId, category);

        UserBehavior b = repo.findByUserIdAndCategory(userId, category);

        if (b == null) {
            repo.save(new UserBehavior(userId, category, 1));
        } else {
            b.setClicks(b.getClicks() + 1);
            b.setLastUpdated(new Date());
            repo.save(b);
        }
    }

    /* ======================================================
       DECISION TREE: predict top category
    ====================================================== */
    public String getMostViewedCategory(String userId) {

        List<UserBehavior> list = repo.findByUserIdOrderByClicksDesc(userId);

        if (list == null || list.isEmpty())
            return null;

        return list.get(0).getCategory();
    }
}
