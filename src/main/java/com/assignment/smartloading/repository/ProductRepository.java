package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    //find one product
    Product findByProductId(String productId);

    //find products by category
    List<Product> findByCategory(String category);

    //find all distinct categories
    @Query(value = "SELECT DISTINCT category FROM products", nativeQuery = true)
    List<String> findDistinctCategories();
}
