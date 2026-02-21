package com.todolist.todolist.features.annonces.mapper;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.categories.mapper.CategoryMapper;
import com.todolist.todolist.features.users.mappers.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface AnnonceMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "categoryEntity", ignore = true)
    AnnonceEntity toEntity(AnnonceDTO dto);

    @Mapping(target = "author_id", ignore = true)
    @Mapping(target = "category_id", ignore = true)
    @Mapping(source = "categoryEntity", target = "category")
    AnnonceDTO toDTO(AnnonceEntity entity);
}
