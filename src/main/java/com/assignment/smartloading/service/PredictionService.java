package com.assignment.smartloading.service;

import com.assignment.smartloading.model.UserBehavior;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PredictionService {

    @Autowired
    private UserBehaviorRepository repo;

    public String predictCategory(String userId) {

        List<UserBehavior> list = repo.findAllByUserId(userId);

        if (list == null || list.isEmpty()) {
            return "popular";
        }

        UserBehavior recent = list.stream()
                .sorted((a,b) -> b.getLastUpdated().compareTo(a.getLastUpdated()))
                .findFirst()
                .orElse(null);

        if (recent != null && recent.getClicks() >= 3) {
            return recent.getCategory();
        }

        UserBehavior max = list.stream()
                .max((a,b) -> Integer.compare(a.getClicks(), b.getClicks()))
                .orElse(null);

        if (max != null && max.getClicks() > 0) {
            return max.getCategory();
        }

        return "popular";
    }
}

