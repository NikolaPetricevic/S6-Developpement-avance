package com.todolist.todolist.features.annonces.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnnonceNotFoundException extends AnnonceException {

    public AnnonceNotFoundException() { super("Annonce not found"); }

}
