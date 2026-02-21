package com.todolist.todolist.features.categories.service;

import com.todolist.todolist.features.categories.dto.CategoryDTO;
import com.todolist.todolist.features.categories.exceptions.CategoryNotFoundException;
import com.todolist.todolist.features.categories.mapper.CategoryMapper;
import com.todolist.todolist.features.categories.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> findAll() {
        return this.categoryRepository.findAll().stream().map(
                category -> {
                    return categoryMapper.toDTO(category);
                }
        ).toList();
    }

    @Override
    public CategoryDTO findOne(Long id) {
        return this.categoryRepository.findById(id).map(
                category -> {
                    return categoryMapper.toDTO(category);
                }
        ).orElseThrow(CategoryNotFoundException::new);
    }
}
