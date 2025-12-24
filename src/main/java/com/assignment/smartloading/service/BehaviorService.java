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
    private final BehaviorCache cache;
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

    //postgreSQL = source of truth. redis only updated after db commit.
    @Transactional
    public void addClick(String userId, String productId, String category) {

        //database write (transactional)
        UserBehavior existing = repo.findByUserIdAndCategory(userId, category);
        if (existing == null) {
            repo.save(new UserBehavior(userId, category, 1));
        } else {
            existing.setClicks(existing.getClicks() + 1);
            repo.save(existing);
        }

        //post-commit work, only after database confirmed
        Runnable afterCommit = () -> {
            // log
            try {
                logger.logClick(userId, productId, category);
            } catch (Exception e) {
                log.warn("Failed to write click log userId={}, productId={}", userId, productId, e);
            }

            // cache update (Redis in k8s, no redis in local)
            try {
                cache.recordClick(userId, category);
            } catch (Exception e) {
                log.warn("Cache click sync failed userId={}, category={}", userId, category, e);
            }

            //evict decision tree so next dashboard uses fresh behavior
            try {
                recommendationService.evictDecisionTreeForUser(userId);
            } catch (Exception e) {
                log.warn("Failed to evict decision-tree cache userId={}", userId, e);
            }
        };

        // ensure it only runs after commit
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    afterCommit.run();
                }
            });
        } else {
            //if transaction sync is not active, fallback
            afterCommit.run();
        }
    }
}
