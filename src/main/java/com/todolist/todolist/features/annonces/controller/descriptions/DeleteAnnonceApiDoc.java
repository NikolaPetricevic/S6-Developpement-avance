package com.todolist.todolist.features.annonces.controller.descriptions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Supprimer une annonce",
        description = "Supprime définitivement une annonce à partir de son identifiant."
)
@ApiResponses({
        @ApiResponse(responseCode = "204", description = "Annonce supprimée avec succès", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content),
        @ApiResponse(responseCode = "404", description = "Annonce introuvable", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
})
public @interface DeleteAnnonceApiDoc {
}
