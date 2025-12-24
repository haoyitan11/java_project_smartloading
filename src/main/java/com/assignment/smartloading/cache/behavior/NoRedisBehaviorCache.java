package com.assignment.smartloading.cache.behavior;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class NoRedisBehaviorCache implements BehaviorCache {
    @Override
    public void recordClick(String userId, String category) {
        //no interaction
    }
}
