package com.assignment.smartloading.service;

import com.assignment.smartloading.model.Product;
import com.assignment.smartloading.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    public Product getProductById(String productId) {
        return productRepository.findByProductId(productId);
    }
}
