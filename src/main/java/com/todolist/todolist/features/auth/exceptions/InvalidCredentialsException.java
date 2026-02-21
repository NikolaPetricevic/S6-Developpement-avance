package com.todolist.todolist.features.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends AuthException {
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}
