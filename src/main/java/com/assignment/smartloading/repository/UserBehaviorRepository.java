package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    //update clicks per user + category
    UserBehavior findByUserIdAndCategory(String userId, String category);

    //top clicked categories for this user
    @Query(value =
            "SELECT category " +
                    "FROM user_behavior " +
                    "WHERE user_id = :userId " +
                    "GROUP BY category " +
                    "ORDER BY SUM(clicks) DESC",
            nativeQuery = true)
    List<String> findUserMostClickedCategories(String userId);

    //top clicked categories for all users
    @Query(value =
            "SELECT category " +
                    "FROM user_behavior " +
                    "GROUP BY category " +
                    "ORDER BY SUM(clicks) DESC",
            nativeQuery = true)
    List<String> findGlobalTopClickedCategories();
}
