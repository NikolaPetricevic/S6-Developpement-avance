package com.todolist.todolist.features.annonces.controller;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.annonces.service.AnnonceService;
import com.todolist.todolist.utils.PaginatedResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/annonces")
public class AnnonceController {

    private final AnnonceService annonceService = new AnnonceService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAnnonces(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("keyword") String keyword,
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("status") StatusEnum status) {

        PaginatedResponse<AnnonceDTO> response = this.annonceService.searchAnnonces(
                keyword, categoryId, status, page, size);

        return Response.ok().entity(response).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnonce(@PathParam("id") Long id) {
        AnnonceDTO annonceDTO = this.annonceService.findOne(id);
        return Response.ok().entity(annonceDTO).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAnnonce(@Valid AnnonceDTO annonceDTO) {
        AnnonceDTO createdAnnonceDTO = this.annonceService.createAnnonce(annonceDTO);
        return Response.ok().entity(createdAnnonceDTO).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAnnonce(
            @PathParam("id") Long id,
            @Valid AnnonceDTO annonceDTO) {

        AnnonceDTO updatedAnnonce = this.annonceService.updateAnnonce(id, annonceDTO);
        return Response.ok().entity(updatedAnnonce).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAnnonce(@PathParam("id") Long id) {
        this.annonceService.deleteAnnonce(id);
        return Response.noContent().build();
    }
}
