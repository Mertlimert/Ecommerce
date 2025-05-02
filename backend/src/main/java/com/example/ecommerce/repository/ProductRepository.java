package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.name LIKE %:keyword% OR " +
           "p.description LIKE %:keyword%")
    Page<Product> search(String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < 10")
    List<Product> findLowStockProducts();
}