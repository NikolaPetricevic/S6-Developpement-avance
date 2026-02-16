package com.todolist.todolist.features.annonces.exceptions;

import jakarta.ws.rs.ForbiddenException;

public class NotArchivedException extends ForbiddenException {
    public NotArchivedException(String message) {
        super(message);
    }
}
