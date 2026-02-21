package com.todolist.todolist.features.categories.repository;

import com.todolist.todolist.features.categories.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

}
