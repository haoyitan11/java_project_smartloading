package com.assignment.smartloading.service;

import com.assignment.smartloading.cache.like.LikeCache;
import com.assignment.smartloading.logging.JsonLoggerService;
import com.assignment.smartloading.model.ProductLike;
import com.assignment.smartloading.repository.ProductLikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private static final Logger log = LoggerFactory.getLogger(LikeService.class);

    private final ProductLikeRepository repo;
    private final JsonLoggerService logger;
    private final LikeCache cache;
    private final UnifiedRecommendationService recommendationService;

    public LikeService(ProductLikeRepository repo,
                       JsonLoggerService logger,
                       LikeCache cache,
                       UnifiedRecommendationService recommendationService) {
        this.repo = repo;
        this.logger = logger;
        this.cache = cache;
        this.recommendationService = recommendationService;
    }

    private Set<String> loadUserLikesFromDB(String userId) {
        var list = repo.findByUserId(userId);
        return list.stream().map(ProductLike::getProductId).collect(Collectors.toSet());
    }

    @Transactional
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

        try { logger.logLike(userId, productId, nowLiked); } catch (Exception ignored) {}

        Runnable afterCommitWork = () -> {
            try {
                // if run with kubernetes, does real work, if local then no interaction
                cache.evictUserLikes(userId);

                long dbCount = repo.countByProductId(productId);
                cache.cacheProductLikesCount(productId, dbCount);
            } catch (Exception e) {
                log.warn("Like cache sync failed after commit userId={}, productId={}", userId, productId, e);
            }

            try { recommendationService.evictDecisionTreeForUser(userId); } catch (Exception ignored) {}
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { afterCommitWork.run(); }
            });
        } else {
            afterCommitWork.run();
        }

        return nowLiked;
    }

    public boolean isLiked(String userId, String productId) {
        try {
            if (cache.hasUserLikesKey(userId)) {
                Set<String> ids = cache.getUserLikes(userId);
                return ids != null && ids.contains(productId);
            }

            Set<String> fromDB = loadUserLikesFromDB(userId);
            cache.cacheUserLikes(userId, fromDB);
            return fromDB.contains(productId);

        } catch (Exception e) {
            return repo.existsByUserIdAndProductId(userId, productId);
        }
    }

    public long getLikes(String productId) {
        try {
            Long cached = cache.getProductLikesCount(productId);
            if (cached != null) return cached;

            long dbCount = repo.countByProductId(productId);
            cache.cacheProductLikesCount(productId, dbCount);
            return dbCount;

        } catch (Exception e) {
            return repo.countByProductId(productId);
        }
    }

    public Map<String, Map<String, Object>> fetchLikeSummaryForProducts(String userId, List<String> productIds) {
        Map<String, Map<String, Object>> out = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) return out;

        Set<String> likedSet;
        try {
            if (cache.hasUserLikesKey(userId)) {
                likedSet = Optional.ofNullable(cache.getUserLikes(userId)).orElse(Collections.emptySet());
            } else {
                likedSet = loadUserLikesFromDB(userId);
                cache.cacheUserLikes(userId, likedSet);
            }
        } catch (Exception e) {
            likedSet = loadUserLikesFromDB(userId);
        }

        List<Long> cachedCounts;
        try {
            cachedCounts = cache.getProductLikesCounts(productIds);
        } catch (Exception e) {
            cachedCounts = Collections.nCopies(productIds.size(), null);
        }

        for (int i = 0; i < productIds.size(); i++) {
            String pid = productIds.get(i);

            long totalLikes;
            Long c = (cachedCounts.size() > i) ? cachedCounts.get(i) : null;

            if (c != null) totalLikes = c;
            else {
                totalLikes = repo.countByProductId(pid);
                try { cache.cacheProductLikesCount(pid, totalLikes); } catch (Exception ignored) {}
            }

            boolean liked = likedSet.contains(pid);

            out.put(pid, Map.of("liked", liked, "totalLikes", totalLikes));
        }

        return out;
    }
}
