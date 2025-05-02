package com.example.ecommerce.config;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        // Varsayılan rolleri oluştur
        createRoles();
        
        // Admin kullanıcı oluştur
        createAdminUser();
        
        // Örnek kategoriler oluştur
        createSampleCategories();
        
        // Örnek ürünler oluştur
        createSampleProducts();
    }
    
    private void createRoles() {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);
            roleRepository.save(userRole);
            
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            
            System.out.println("Roller başarıyla oluşturuldu!");
        }
    }
    
    private void createAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setAddress("Admin Address");
            admin.setPhoneNumber("5551234567");
            
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(roles::add);
            roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add);
            admin.setRoles(roles);
            
            userRepository.save(admin);
            
            System.out.println("Admin kullanıcı başarıyla oluşturuldu!");
        }
    }
    
    private void createSampleCategories() {
        if (categoryRepository.count() == 0) {
            Category category1 = new Category();
            category1.setName("Elektronik");
            category1.setDescription("Her türlü elektronik cihaz");
            categoryRepository.save(category1);
            
            Category category2 = new Category();
            category2.setName("Giyim");
            category2.setDescription("Erkek ve kadın giyim ürünleri");
            categoryRepository.save(category2);
            
            Category category3 = new Category();
            category3.setName("Kitap");
            category3.setDescription("Her türlü kitap ve dergi");
            categoryRepository.save(category3);
            
            System.out.println("Örnek kategoriler başarıyla oluşturuldu!");
        }
    }
    
    private void createSampleProducts() {
        if (productRepository.count() == 0) {
            Category elektronik = categoryRepository.findByName("Elektronik").orElse(null);
            Category giyim = categoryRepository.findByName("Giyim").orElse(null);
            Category kitap = categoryRepository.findByName("Kitap").orElse(null);
            
            if (elektronik != null) {
                Product product1 = new Product();
                product1.setName("Akıllı Telefon");
                product1.setDescription("Üst düzey akıllı telefon");
                product1.setPrice(new BigDecimal("12999.99"));
                product1.setStockQuantity(50);
                product1.setImageUrl("https://via.placeholder.com/200x300");
                product1.setCategory(elektronik);
                productRepository.save(product1);
                
                Product product2 = new Product();
                product2.setName("Laptop");
                product2.setDescription("Yüksek performanslı dizüstü bilgisayar");
                product2.setPrice(new BigDecimal("24999.99"));
                product2.setStockQuantity(30);
                product2.setImageUrl("https://via.placeholder.com/200x300");
                product2.setCategory(elektronik);
                productRepository.save(product2);
            }
            
            if (giyim != null) {
                Product product3 = new Product();
                product3.setName("T-Shirt");
                product3.setDescription("Pamuklu t-shirt");
                product3.setPrice(new BigDecimal("299.99"));
                product3.setStockQuantity(100);
                product3.setImageUrl("https://via.placeholder.com/200x300");
                product3.setCategory(giyim);
                productRepository.save(product3);
                
                Product product4 = new Product();
                product4.setName("Kot Pantolon");
                product4.setDescription("Slim fit kot pantolon");
                product4.setPrice(new BigDecimal("499.99"));
                product4.setStockQuantity(80);
                product4.setImageUrl("https://via.placeholder.com/200x300");
                product4.setCategory(giyim);
                productRepository.save(product4);
            }
            
            if (kitap != null) {
                Product product5 = new Product();
                product5.setName("Java Programlama");
                product5.setDescription("Java programlama hakkında detaylı bir kitap");
                product5.setPrice(new BigDecimal("149.99"));
                product5.setStockQuantity(40);
                product5.setImageUrl("https://via.placeholder.com/200x300");
                product5.setCategory(kitap);
                productRepository.save(product5);
                
                Product product6 = new Product();
                product6.setName("Spring Boot İle Uygulama Geliştirme");
                product6.setDescription("Spring Boot framework'ü ile web uygulamaları geliştirme");
                product6.setPrice(new BigDecimal("199.99"));
                product6.setStockQuantity(25);
                product6.setImageUrl("https://via.placeholder.com/200x300");
                product6.setCategory(kitap);
                productRepository.save(product6);
            }
            
            System.out.println("Örnek ürünler başarıyla oluşturuldu!");
        }
    }
}