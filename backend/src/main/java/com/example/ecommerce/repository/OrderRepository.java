package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByStatus(String status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    BigDecimal getTotalSalesForPeriod(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    Long getOrderCountForPeriod(LocalDateTime start, LocalDateTime end);
}