package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    UserBehavior findByUserIdAndCategory(String userId, String category);

    List<UserBehavior> findByUserIdOrderByClicksDesc(String userId);

    List<UserBehavior> findAllByUserId(String userId);
}
