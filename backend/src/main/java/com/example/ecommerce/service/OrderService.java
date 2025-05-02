package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.OrderRequest;
import com.example.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> getAllOrders();
    
    Page<Order> getAllOrdersPaged(Pageable pageable);
    
    Page<Order> getUserOrders(Long userId, Pageable pageable);
    
    Order getOrderById(Long id);
    
    Order createOrder(Long userId, OrderRequest orderRequest);
    
    Order updateOrderStatus(Long id, String status);
    
    void deleteOrder(Long id);
    
    BigDecimal getTotalSalesForPeriod(LocalDateTime start, LocalDateTime end);
    
    Long getOrderCountForPeriod(LocalDateTime start, LocalDateTime end);
    
    Map<String, Object> getOrderStatistics(LocalDateTime start, LocalDateTime end);
    
    List<Order> getOrdersByStatus(String status);
}