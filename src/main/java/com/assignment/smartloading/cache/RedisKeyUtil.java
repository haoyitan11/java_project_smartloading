package com.assignment.smartloading.cache;

public final class RedisKeyUtil {
    private RedisKeyUtil() {}

    //product
    public static String productJson(String productId) { return "product:json:" + requireNotEmpty(productId); }
    public static String productLikes(String productId) { return "product:likes:" + requireNotEmpty(productId); }

    //user
    public static String userLikes(String userId) { return "user:likes:" + requireNotEmpty(userId); }

    //clicks
    public static String userClicks(String userId) { return "user:clicks:hash:" + requireNotEmpty(userId); }
    public static String userCategoryZset(String userId) { return "user:category:zset:" + requireNotEmpty(userId); }
    public static String globalCategoryZset() { return "global:category:zset"; }

    //decision tree
    public static String decisionTreeFinal(String userId) { return "decisionTree:final:" + requireNotEmpty(userId); }
    public static String decisionTreeMeta(String userId) { return "decisionTree:meta:" + requireNotEmpty(userId); }
    public static String decisionTreeDebug(String userId) { return "decisionTree:debug:" + requireNotEmpty(userId); }

    private static String requireNotEmpty(String s) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException("Redis key segment must not be null/empty");
        }
        return s;
    }
}
