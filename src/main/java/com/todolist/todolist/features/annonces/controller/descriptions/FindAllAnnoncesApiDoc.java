package com.todolist.todolist.features.annonces.controller.descriptions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Lister les annonces",
        description = "Retourne une liste paginée d'annonces, avec filtres optionnels sur le mot-clé, le statut, la catégorie, l'auteur et les dates."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Liste récupérée avec succès",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Page.class)
                )
        ),
        @ApiResponse(responseCode = "400", description = "Paramètres de filtre ou de pagination invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
})
public @interface FindAllAnnoncesApiDoc {
}
