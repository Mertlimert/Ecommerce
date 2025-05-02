package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotBlank
    private String shippingAddress;
    
    @NotBlank
    private String paymentMethod;
    
    // Toplam tutar opsiyonel olabilir (sepet tutarı kullanılabilir)
    private BigDecimal totalAmount;
    
    // Not: Sepetteki ürünler sipariş oluşturulurken OrderItem'lara dönüştürülecek
}