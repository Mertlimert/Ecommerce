package com.example.ecommerce.service;

import com.example.ecommerce.model.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    
    Category getCategoryById(Long id);
    
    Category createCategory(Category category);
    
    Category updateCategory(Long id, Category category);
    
    void deleteCategory(Long id);
    
    boolean existsByName(String name);
}