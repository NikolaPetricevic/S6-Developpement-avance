package com.todolist.todolist.categories.service;

import com.todolist.todolist.categories.entity.Category;
import com.todolist.todolist.categories.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
