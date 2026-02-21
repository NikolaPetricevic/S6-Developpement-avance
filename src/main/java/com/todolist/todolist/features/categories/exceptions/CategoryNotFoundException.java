package com.todolist.todolist.features.categories.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends CategoryException{

    public CategoryNotFoundException() { super("Category not found"); }
}
