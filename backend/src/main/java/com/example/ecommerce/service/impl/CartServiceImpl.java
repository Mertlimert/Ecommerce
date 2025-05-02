package com.example.ecommerce.service.impl;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Cart getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
                
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setTotalAmount(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }
    
    @Override
    @Transactional
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Miktar pozitif bir değer olmalıdır.");
        }
        
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + productId));
        
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        
        cartItem.setPrice(product.getPrice());
        cartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        
        cartItemRepository.save(cartItem);
        
        updateCartTotalAmount(cart);
        
        return cart;
    }
    
    @Override
    @Transactional
    public Cart updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Miktar pozitif bir değer olmalıdır.");
        }
        
        Cart cart = getCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sepet öğesi bulunamadı: " + cartItemId));
        
        if (!cart.getId().equals(cartItem.getCart().getId())) {
            throw new IllegalArgumentException("Bu sepet öğesi kullanıcının sepetinde değil.");
        }
        
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        
        cartItemRepository.save(cartItem);
        
        updateCartTotalAmount(cart);
        
        return cart;
    }
    
    @Override
    @Transactional
    public Cart removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sepet öğesi bulunamadı: " + cartItemId));
        
        if (!cart.getId().equals(cartItem.getCart().getId())) {
            throw new IllegalArgumentException("Bu sepet öğesi kullanıcının sepetinde değil.");
        }
        
        cartItemRepository.delete(cartItem);
        
        updateCartTotalAmount(cart);
        
        return cart;
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        
        cartItemRepository.deleteByCartId(cart.getId());
        
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
    
    @Override
    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cartItemRepository.findByCartId(cart.getId());
    }
    
    private void updateCartTotalAmount(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }
}