package com.todolist.todolist.features.annonces.utils;

import com.todolist.todolist.exceptions.InvalidSortFieldException;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AnnonceSortValidator {

    private static final Set<String> ALLOWED_FIELDS = Arrays.stream(AnnonceEntity.class.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toSet());

    public void validate(String sortBy) {
        if (!ALLOWED_FIELDS.contains(sortBy)) {
            throw new InvalidSortFieldException(sortBy, ALLOWED_FIELDS);
        }
    }
}
