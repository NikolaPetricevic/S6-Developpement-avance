package com.todolist.todolist.exceptions;

import com.todolist.todolist.features.annonces.exceptions.AnnonceException;
import com.todolist.todolist.features.auth.exceptions.AuthException;
import com.todolist.todolist.features.categories.exceptions.CategoryException;
import com.todolist.todolist.features.users.exceptions.UserException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AnnonceException.class)
    public ResponseEntity<ErrorResponseDTO> handleAnnonceException(AnnonceException ex, HttpServletRequest request) {
        return buildError(ex, request);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserException(UserException ex, HttpServletRequest request) {
        return buildError(ex, request);
    }

    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<ErrorResponseDTO> handleCategoryException(CategoryException ex, HttpServletRequest request) {
        return buildError(ex, request);
    }

    private ResponseEntity<ErrorResponseDTO> buildError(RuntimeException ex, HttpServletRequest request) {
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidSortFieldException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidSortFieldException(InvalidSortFieldException ex, HttpServletRequest request) {
        return buildError(ex, request);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsException(AuthException ex, HttpServletRequest request) {
        return buildError(ex, request);
    }
}
