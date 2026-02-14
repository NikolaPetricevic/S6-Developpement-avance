package com.todolist.todolist.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 500);
        errorResponse.put("message", "Internal server error");
        errorResponse.put("details", exception.getMessage());

        exception.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }
}
