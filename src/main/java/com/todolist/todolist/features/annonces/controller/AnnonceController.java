package com.todolist.todolist.features.annonces.controller;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.annonces.service.AnnonceService;
import com.todolist.todolist.features.auth.service.AuthService;
import com.todolist.todolist.features.users.entity.User;
import com.todolist.todolist.utils.PaginatedResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Path("/annonces")
@SecurityRequirement(name = "bearerAuth")
public class AnnonceController {

    private final AnnonceService annonceService = new AnnonceService();
    private final AuthService authService = new AuthService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAnnonces(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("keyword") String keyword,
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("status") StatusEnum status,
            @Parameter(hidden = true) @HeaderParam("Authorization") String authHeader) {

        log.info("GET /api/annonces");

        String token = authService.extractToken(authHeader);
        if (!authService.isValidToken(token)) {
            log.error("Response : 401 Unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Unauthorized"))
                    .build();
        }

        PaginatedResponse<AnnonceDTO> response = this.annonceService.searchAnnonces(
                keyword, categoryId, status, page, size);

        log.info("Response : 200 OK");
        return Response.ok().entity(response).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnonce(@PathParam("id") Long id,
                               @Parameter(hidden = true) @HeaderParam("Authorization") String authHeader) {

        log.info("GET /api/annonces/{}", id);

        String token = authService.extractToken(authHeader);
        if (!authService.isValidToken(token)) {
            log.error("Response : 401 Unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Unauthorized"))
                    .build();
        }

        AnnonceDTO annonceDTO = this.annonceService.findOne(id);

        log.info("Response : 200 OK");
        return Response.ok().entity(annonceDTO).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAnnonce(@Valid AnnonceDTO annonceDTO,
                                  @Parameter(hidden = true) @HeaderParam("Authorization") String authHeader) {

        log.info("POST /api/annonces");

        String token = authService.extractToken(authHeader);
        if (!authService.isValidToken(token)) {
            log.error("Response : 401 Unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Unauthorized"))
                    .build();
        }


        log.error("Response : 201 Created");
        AnnonceDTO createdAnnonceDTO = this.annonceService.createAnnonce(annonceDTO);
        return Response.status(Response.Status.CREATED).entity(createdAnnonceDTO).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAnnonce(
            @PathParam("id") Long id,
            @Valid AnnonceDTO annonceDTO,
            @Parameter(hidden = true) @HeaderParam("Authorization") String authHeader) {

        log.info("PUT /api/annonces/{}", id);

        String token = authService.extractToken(authHeader);

        User currentUser = authService.getCurrentUser(token);
        if (currentUser == null) {
            log.error("Response : 401 Unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Unauthorized"))
                    .build();
        }

        try {
            AnnonceDTO updatedAnnonce = this.annonceService.updateAnnonce(id, annonceDTO, currentUser.getId());

            log.info("Response : 200 OK");
            return Response.ok().entity(updatedAnnonce).build();
        } catch (ForbiddenException e) {
            log.info("Response : 403 Forbidden");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
        catch (OptimisticLockException e) {
            log.info("Response : 409 Conflict");
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "This annonce has been modified by another user"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAnnonce(@PathParam("id") Long id,
                                  @Parameter(hidden = true) @HeaderParam("Authorization") String authHeader) {

        log.info("DELETE /api/annonces/{}", id);

        String token = authService.extractToken(authHeader);

        User currentUser = authService.getCurrentUser(token);
        if (currentUser == null) {
            log.error("Response : 401 Unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Unauthorized"))
                    .build();
        }

        try {
            this.annonceService.deleteAnnonce(id, currentUser.getId());
            log.info("Response : 204 No Content");
            return Response.noContent().build();
        } catch (ForbiddenException e) {
            log.info("Response : 403 Forbidden");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

}
