package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    Optional<ProductLike> findByUserIdAndProductId(String userId, String productId);

    long countByProductId(String productId);

    void deleteByUserIdAndProductId(String userId, String productId);

    boolean existsByUserIdAndProductId(String userId, String productId);
}
