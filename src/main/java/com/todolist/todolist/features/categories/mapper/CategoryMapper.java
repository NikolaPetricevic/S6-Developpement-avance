package com.todolist.todolist.features.categories.mapper;

import com.todolist.todolist.features.categories.dto.CategoryDTO;
import com.todolist.todolist.features.categories.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryEntity toEntity(CategoryDTO categoryDTO);

    CategoryDTO toDTO(CategoryEntity categoryEntity);
}
