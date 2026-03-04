package com.todolist.todolist.features.annonces.controller.descriptions;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Récupérer une annonce",
        description = "Retourne le détail d'une annonce à partir de son identifiant."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Annonce trouvée",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AnnonceDTO.class)
                )
        ),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content),
        @ApiResponse(responseCode = "404", description = "Annonce introuvable", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
})
public @interface FindOneAnnonceApiDoc {
}
