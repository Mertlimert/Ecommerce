package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.security.services.UserDetailsImpl;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {

    @Autowired
    private CartService cartService;

    // Mevcut kullanıcının sepetini getir
    @GetMapping
    public ResponseEntity<Cart> getCurrentUserCart() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // Sepet içindeki öğeleri listele
    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    // Sepete ürün ekle
    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        return ResponseEntity.ok(cartService.addItemToCart(userId, productId, quantity));
    }

    // Sepetteki bir ürünün miktarını güncelle
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        return ResponseEntity.ok(cartService.updateCartItem(userId, itemId, quantity));
    }

    // Sepetten bir ürünü kaldır
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long itemId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }

    // Sepeti tamamen boşalt
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Boolean>> clearCart() {
        Long userId = getCurrentUserId();
        cartService.clearCart(userId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("cleared", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    // JWT token'dan mevcut kullanıcının ID'sini alma yardımcı metodu
    private Long getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl user = (UserDetailsImpl) userDetails;
        return user.getId();
    }
}