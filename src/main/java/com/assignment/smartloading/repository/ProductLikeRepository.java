package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    Optional<ProductLike> findByUserIdAndProductId(String userId, String productId);

    long countByProductId(String productId);

    boolean existsByUserIdAndProductId(String userId, String productId);

    void deleteByUserIdAndProductId(String userId, String productId);

    // Most liked categories by a specific user
    @Query("""
        SELECT p.category
        FROM ProductLike pl JOIN Product p ON pl.productId = p.productId
        WHERE pl.userId = :userId
        GROUP BY p.category
        ORDER BY COUNT(p.category) DESC
    """)
    List<String> findUserMostLikedCategories(String userId);

    // Global: Most liked categories by ALL users
    @Query("""
        SELECT p.category
        FROM ProductLike pl JOIN Product p ON pl.productId = p.productId
        GROUP BY p.category
        ORDER BY COUNT(p.category) DESC
    """)
    List<String> findGlobalTopLikedCategories();
}
