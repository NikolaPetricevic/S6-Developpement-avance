package com.todolist.todolist.features.categories.service;

import com.todolist.todolist.features.categories.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    public List<CategoryDTO> findAll();

    public CategoryDTO findOne(Long id);
}
