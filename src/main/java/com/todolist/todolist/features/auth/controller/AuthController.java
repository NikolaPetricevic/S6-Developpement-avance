package com.todolist.todolist.features.auth.controller;

import com.todolist.todolist.features.auth.dto.LoginDTO;
import com.todolist.todolist.features.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/auth")
public class AuthController {

    private final AuthService authService = new AuthService();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginDTO loginRequest) {
        LoginDTO authResponse = authService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        if (authResponse != null) {
            return Response.ok().entity(authResponse).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", "Invalid credentials"))
                .build();
    }


}
