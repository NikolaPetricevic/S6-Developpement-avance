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
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Path("/auth")
public class AuthController {

    private final AuthService authService = new AuthService();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginDTO loginRequest) {

        log.info("GET /api/auth/login");

        LoginDTO authResponse = authService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        if (authResponse != null) {
            log.info("Response : 200 OK");
            return Response.ok().entity(authResponse).build();
        }

        log.error("Response : 401 Unauthorized");
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", "Invalid credentials"))
                .build();
    }


}
