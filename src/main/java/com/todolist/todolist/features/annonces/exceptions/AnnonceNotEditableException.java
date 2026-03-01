package com.todolist.todolist.features.annonces.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class AnnonceNotEditableException extends AnnonceException {

    public AnnonceNotEditableException() {
        super("A published annonce cannot be edited.");
    }

}
