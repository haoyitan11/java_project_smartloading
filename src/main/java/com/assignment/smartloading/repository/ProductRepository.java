package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Load the top 15 products for recommendation stage 0.
     */
    @Query(value = "SELECT * FROM products ORDER BY product_id LIMIT 15", nativeQuery = true)
    List<Product> findTop15();

    /**
     * Find a single product by productId (matches ProductService.getProductById).
     */
    Product findByProductId(String productId);

    /**
     * Load products by category.
     */
    List<Product> findByCategory(String category);

    /**
     * Load multiple categories at once.
     */
    List<Product> findByCategoryIn(List<String> categories);

    /**
     * For analytics: get all distinct categories.
     */
    @Query(value = "SELECT DISTINCT category FROM products", nativeQuery = true)
    List<String> findDistinctCategories();

    /**
     * Search function (optional).
     */
    @Query(value = "SELECT * FROM products WHERE LOWER(product_name) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<Product> searchByName(String keyword);
}
