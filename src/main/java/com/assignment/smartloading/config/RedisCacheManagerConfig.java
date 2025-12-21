package com.assignment.smartloading.config;

import com.assignment.smartloading.dto.DecisionTreeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheManagerConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory, ObjectMapper objectMapper) {

        GenericJackson2JsonRedisSerializer genericSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisSerializationContext.SerializationPair<String> keyPair =
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());

        RedisSerializationContext.SerializationPair<Object> defaultValuePair =
                RedisSerializationContext.SerializationPair.fromSerializer(genericSerializer);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(keyPair)
                .serializeValuesWith(defaultValuePair)
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues();

        Jackson2JsonRedisSerializer<DecisionTreeResult> dtSerializer =
                new Jackson2JsonRedisSerializer<>(DecisionTreeResult.class);
        dtSerializer.setObjectMapper(objectMapper);

        @SuppressWarnings("unchecked")
        RedisSerializationContext.SerializationPair<Object> dtValuePair =
                (RedisSerializationContext.SerializationPair<Object>) (RedisSerializationContext.SerializationPair<?>)
                        RedisSerializationContext.SerializationPair.fromSerializer(dtSerializer);

        RedisCacheConfiguration dtConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(keyPair)
                .serializeValuesWith(dtValuePair)
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("decisionTreeResult", dtConfig);

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .transactionAware()
                .build();
    }
}
