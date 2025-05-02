package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.List;

public interface CartService {
    Cart getCartByUserId(Long userId);
    
    Cart addItemToCart(Long userId, Long productId, Integer quantity);
    
    Cart updateCartItem(Long userId, Long cartItemId, Integer quantity);
    
    Cart removeItemFromCart(Long userId, Long cartItemId);
    
    void clearCart(Long userId);
    
    List<CartItem> getCartItems(Long userId);
}