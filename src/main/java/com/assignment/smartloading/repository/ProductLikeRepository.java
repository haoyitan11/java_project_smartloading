package com.assignment.smartloading.repository;

import com.assignment.smartloading.dto.ProductLikeSummary;
import com.assignment.smartloading.model.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    //find user ID and product ID
    Optional<ProductLike> findByUserIdAndProductId(String userId, String productId);

    long countByProductId(String productId);

    boolean existsByUserIdAndProductId(String userId, String productId);

    //find likeService later on
    List<ProductLike> findByUserId(String userId);

    // top liked categories for personal user
    @Query("""
        SELECT p.category
        FROM ProductLike pl JOIN Product p ON pl.productId = p.productId
        WHERE pl.userId = :userId
        GROUP BY p.category
        ORDER BY COUNT(p.category) DESC
    """)
    List<String> findUserMostLikedCategories(String userId);

    // global most liked categories
    @Query("""
        SELECT p.category
        FROM ProductLike pl JOIN Product p ON pl.productId = p.productId
        GROUP BY p.category
        ORDER BY COUNT(p.category) DESC
    """)
    List<String> findGlobalTopLikedCategories();

    //dashboard
    @Query("""
        SELECT new com.assignment.smartloading.dto.ProductLikeSummary(
            p.productId,
            COUNT(plAll.productId),
            MAX(CASE WHEN plUser.userId IS NOT NULL THEN true ELSE false END)
        )
        FROM Product p
        LEFT JOIN ProductLike plAll ON p.productId = plAll.productId
        LEFT JOIN ProductLike plUser ON p.productId = plUser.productId AND plUser.userId = :userId
        WHERE p.productId IN :productIds
        GROUP BY p.productId
    """)
    List<ProductLikeSummary> fetchLikeSummary(
            @Param("userId") String userId,
            @Param("productIds") List<String> productIds
    );
}
