package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory(String category);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();

    Product findByProductId(String productId);
}
