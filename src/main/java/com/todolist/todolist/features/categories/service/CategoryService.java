package com.todolist.todolist.features.categories.service;

import com.todolist.todolist.features.categories.entity.Category;
import com.todolist.todolist.features.categories.repository.CategoryRepository;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService() {
        this.categoryRepository = new CategoryRepository();
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findOne(Long id) {
        return categoryRepository.findOne(id);
    }
}
