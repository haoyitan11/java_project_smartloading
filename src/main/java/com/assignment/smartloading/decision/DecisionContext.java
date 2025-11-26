package com.assignment.smartloading.decision;

import com.assignment.smartloading.model.UserBehavior;
import java.util.List;

public class DecisionContext {

    public final String userId;
    public final List<String> likedCategories;
    public final List<UserBehavior> behavior;
    public final List<String> globalTop;

    public DecisionContext(String userId,
                           List<String> likedCategories,
                           List<UserBehavior> behavior,
                           List<String> globalTop) {

        this.userId = userId;
        this.likedCategories = likedCategories;
        this.behavior = behavior;
        this.globalTop = globalTop;
    }

    public boolean hasLikes() { return likedCategories != null && !likedCategories.isEmpty(); }

    public boolean hasBehavior() { return behavior != null && !behavior.isEmpty(); }
}
