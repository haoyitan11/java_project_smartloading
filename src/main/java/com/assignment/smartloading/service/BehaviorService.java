package com.assignment.smartloading.service;

import com.assignment.smartloading.cache.BehaviorCacheRepository;
import com.assignment.smartloading.logging.JsonLoggerService;
import com.assignment.smartloading.model.UserBehavior;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class BehaviorService {

    private static final Logger log = LoggerFactory.getLogger(BehaviorService.class);

    private final UserBehaviorRepository repo;
    private final JsonLoggerService logger;
    private final BehaviorCacheRepository cache;
    private final UnifiedRecommendationService recommendationService;

    public BehaviorService(UserBehaviorRepository repo,
                           JsonLoggerService logger,
                           BehaviorCacheRepository cache,
                           UnifiedRecommendationService recommendationService) {
        this.repo = repo;
        this.logger = logger;
        this.cache = cache;
        this.recommendationService = recommendationService;
    }

    @Transactional
    public void addClick(String userId, String productId, String category) {
        UserBehavior existing = repo.findByUserIdAndCategory(userId, category);
        if (existing == null) {
            repo.save(new UserBehavior(userId, category, 1));
        } else {
            existing.setClicks(existing.getClicks() + 1);
            repo.save(existing);
        }

        Runnable after = () -> {
            try { logger.logClick(userId, productId, category); } catch (Exception e) {
                log.warn("Failed to write click log", e);
            }
            try { cache.recordClick(userId, category); } catch (Exception e) {
                log.warn("Redis click sync failed", e);
            }
            try { recommendationService.evictDecisionTreeForUser(userId); } catch (Exception ignored) {}
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { after.run(); }
            });
        } else {
            after.run();
        }
    }
}
