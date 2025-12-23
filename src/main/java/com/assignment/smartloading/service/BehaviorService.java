package com.assignment.smartloading.service;

import com.assignment.smartloading.cache.behavior.BehaviorCache;
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
    private final BehaviorCache cache; // Redis or No-op depending on profile
    private final UnifiedRecommendationService recommendationService;

    public BehaviorService(UserBehaviorRepository repo,
                           JsonLoggerService logger,
                           BehaviorCache cache,
                           UnifiedRecommendationService recommendationService) {
        this.repo = repo;
        this.logger = logger;
        this.cache = cache;
        this.recommendationService = recommendationService;
    }

    /**
     * Source-of-truth = PostgreSQL.
     * Redis is only a cache layer updated AFTER DB COMMIT.
     */
    @Transactional
    public void addClick(String userId, String productId, String category) {

        // 1) DB write (transactional)
        UserBehavior existing = repo.findByUserIdAndCategory(userId, category);
        if (existing == null) {
            repo.save(new UserBehavior(userId, category, 1));
        } else {
            existing.setClicks(existing.getClicks() + 1);
            repo.save(existing);
        }

        // 2) Post-commit work (only after DB confirmed)
        Runnable afterCommit = () -> {
            // (a) Log (should not break main flow)
            try {
                logger.logClick(userId, productId, category);
            } catch (Exception e) {
                log.warn("Failed to write click log userId={}, productId={}", userId, productId, e);
            }

            // (b) Cache update (Redis in k8s, no-op in local)
            try {
                cache.recordClick(userId, category);
            } catch (Exception e) {
                log.warn("Cache click sync failed userId={}, category={}", userId, category, e);
            }

            // (c) Evict decision tree so next dashboard uses fresh behavior
            try {
                recommendationService.evictDecisionTreeForUser(userId);
            } catch (Exception e) {
                log.warn("Failed to evict decision-tree cache userId={}", userId, e);
            }
        };

        // 3) Ensure it only runs after commit
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    afterCommit.run();
                }
            });
        } else {
            // fallback: if transaction sync is not active, run immediately
            afterCommit.run();
        }
    }
}
