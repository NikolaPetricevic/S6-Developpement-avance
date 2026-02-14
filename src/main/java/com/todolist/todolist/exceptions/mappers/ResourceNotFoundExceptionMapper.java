package com.todolist.todolist.exceptions.mappers;

import com.todolist.todolist.exceptions.ResourceNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;


@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 404);
        errorResponse.put("message", exception.getMessage());

        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .build();
    }
}
