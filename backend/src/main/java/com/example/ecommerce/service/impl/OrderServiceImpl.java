package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.OrderRequest;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartService cartService;
    
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Override
    public Page<Order> getAllOrdersPaged(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    @Override
    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }
    
    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı: " + id));
    }
    
    @Override
    @Transactional
    public Order createOrder(Long userId, OrderRequest orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        
        // Sepetteki ürünleri al
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Sipariş oluşturulamaz, sepet boş!");
        }
        
        // Sipariş oluştur
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("BEKLEMEDE");
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        
        // Toplam tutarı ya sepetten al ya da gelen değeri kullan
        BigDecimal totalAmount = orderRequest.getTotalAmount();
        if (totalAmount == null) {
            totalAmount = cartItems.stream()
                    .map(CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        order.setTotalAmount(totalAmount);
        
        // Sipariş kaydedilmeli ki ID'si oluşsun
        Order savedOrder = orderRepository.save(order);
        
        // Sepet öğelerini sipariş öğelerine dönüştür
        Set<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            return orderItem;
        }).collect(Collectors.toSet());
        
        order.setOrderItems(orderItems);
        
        // Sipariş kaydedildikten sonra sepeti temizle
        cartService.clearCart(userId);
        
        return orderRepository.save(order);
    }
    
    @Override
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    @Override
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
    
    @Override
    public BigDecimal getTotalSalesForPeriod(LocalDateTime start, LocalDateTime end) {
        BigDecimal totalSales = orderRepository.getTotalSalesForPeriod(start, end);
        return totalSales != null ? totalSales : BigDecimal.ZERO;
    }
    
    @Override
    public Long getOrderCountForPeriod(LocalDateTime start, LocalDateTime end) {
        Long orderCount = orderRepository.getOrderCountForPeriod(start, end);
        return orderCount != null ? orderCount : 0L;
    }
    
    @Override
    public Map<String, Object> getOrderStatistics(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalSales", getTotalSalesForPeriod(start, end));
        statistics.put("orderCount", getOrderCountForPeriod(start, end));
        
        // Daha fazla istatistik eklenebilir
        
        return statistics;
    }
    
    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
}