package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    
    Page<Product> getAllProductsPaged(Pageable pageable);
    
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
    
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    Product getProductById(Long id);
    
    Product createProduct(Product product);
    
    Product updateProduct(Long id, Product product);
    
    void deleteProduct(Long id);
    
    List<Product> getLowStockProducts();
}