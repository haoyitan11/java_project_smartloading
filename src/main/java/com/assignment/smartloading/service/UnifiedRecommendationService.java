package com.assignment.smartloading.service;

import com.assignment.smartloading.cache.RedisKeyUtil;
import com.assignment.smartloading.decision.*;
import com.assignment.smartloading.dto.DecisionTreeResult;
import com.assignment.smartloading.dto.ProductDTO;
import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.repository.ProductLikeRepository;
import com.assignment.smartloading.repository.ProductRepository;
import com.assignment.smartloading.repository.UserBehaviorRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UnifiedRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(UnifiedRecommendationService.class);

    @Autowired private ProductRepository productRepo;
    @Autowired private ProductLikeRepository likeRepo;
    @Autowired private UserBehaviorRepository behaviorRepo;

    @Autowired private StringRedisTemplate srt;
    @Autowired private ObjectMapper objectMapper;

    private final DecisionTreeEngine engine = new DecisionTreeEngine();
    private final DecisionNode tree = DecisionTreeBuilder.buildTree(engine);

    private static final Duration DECISION_TTL = Duration.ofMinutes(10);

    //decision result from postgreSQL
    public DecisionTreeResult runDecisionTree(String userId) {
        //Build context from postgreSQL
        RecommendationContext ctx = new RecommendationContext(userId);
        ctx.allProducts = productRepo.findAll();
        ctx.likedCategories = likeRepo.findUserMostLikedCategories(userId);
        ctx.clickedCategories = behaviorRepo.findUserMostClickedCategories(userId);
        LinkedHashSet<String> globals = new LinkedHashSet<>();
        globals.addAll(likeRepo.findGlobalTopLikedCategories());
        globals.addAll(behaviorRepo.findGlobalTopClickedCategories());
        ctx.globalCategories = new ArrayList<>(globals);

        //Evaluate pipeline (postgreSQL)
        tree.evaluate(ctx);

        //gather meta & DTOs
        Map<String, Long> categoryCount = ctx.final15Products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        LinkedHashSet<String> top3 = new LinkedHashSet<>();
        ctx.stage1LikesAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage2ClicksAdded.forEach(p -> top3.add(p.getCategory()));
        ctx.stage3GlobalAdded.forEach(p -> top3.add(p.getCategory()));
        List<String> top3Categories = top3.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));

        List<ProductDTO> initialDtos = ctx.allProducts.stream().map(ProductDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
        List<ProductDTO> s1Dtos = ctx.stage1LikesAdded.stream().map(ProductDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
        List<ProductDTO> s2Dtos = ctx.stage2ClicksAdded.stream().map(ProductDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
        List<ProductDTO> s3Dtos = ctx.stage3GlobalAdded.stream().map(ProductDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
        List<ProductDTO> finalDtos = ctx.final15Products.stream().map(ProductDTO::from)
                .collect(Collectors.toCollection(ArrayList::new));
        List<String> steps = ctx.steps == null ? new ArrayList<>() : new ArrayList<>(ctx.steps);

        DecisionTreeResult result = new DecisionTreeResult(
                initialDtos, s1Dtos, s2Dtos, s3Dtos, finalDtos,
                categoryCount, top3Categories, steps
        );

        //Persist minimal caches to Redis
        try {
            String finalKey = RedisKeyUtil.decisionTreeFinal(userId);
            String metaKey = RedisKeyUtil.decisionTreeMeta(userId);

            List<String> finalIds = finalDtos.stream().map(ProductDTO::getProductId)
                    .collect(Collectors.toCollection(ArrayList::new));
            srt.opsForValue().set(finalKey, objectMapper.writeValueAsString(finalIds), DECISION_TTL);

            Map<String, Object> meta = new HashMap<>();
            meta.put("categoryCount", categoryCount);
            meta.put("top3Categories", top3Categories);
            srt.opsForValue().set(metaKey, objectMapper.writeValueAsString(meta), DECISION_TTL);

            //ensure product JSONs are cached for final products (optional)
            for (ProductDTO dto : finalDtos) {
                String pk = RedisKeyUtil.productJson(dto.getProductId());
                srt.opsForValue().set(pk, objectMapper.writeValueAsString(dto)); // optional: no TTL
            }

            // debug JSON (only for development)
            String debugKey = RedisKeyUtil.decisionTreeDebug(userId);
            srt.opsForValue().set(debugKey, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result), DECISION_TTL);
        } catch (Exception e) {
            log.warn("Failed to write decision caches to Redis - continue, DB is source-of-truth", e);
        }

        return result;
    }

    public void evictDecisionTreeForUser(String userId) {
        try {
            srt.delete(RedisKeyUtil.decisionTreeFinal(userId));
            srt.delete(RedisKeyUtil.decisionTreeMeta(userId));
            srt.delete(RedisKeyUtil.decisionTreeDebug(userId));
            log.info("Evicted decision caches for {}", userId);
        } catch (Exception e) {
            log.warn("Failed to evict decision caches for {}: {}", userId, e.toString());
        }
    }
}
