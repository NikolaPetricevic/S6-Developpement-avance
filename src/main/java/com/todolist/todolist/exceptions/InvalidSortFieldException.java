package com.todolist.todolist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSortFieldException extends RuntimeException {

    public InvalidSortFieldException(String field, Set<String> allowedFields) {
        super("Invalid sort field: '" + field + "'. Allowed fields: " + allowedFields);
    }
}
